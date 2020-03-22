package docpreview.service.impl;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import docpreview.domain.Doc;
import docpreview.pdfbox.tools.PDFToImageUtil;
import docpreview.pdfbox.tools.SHAUtil;
import docpreview.repository.DocRepository;
import docpreview.service.DocService;
import docpreview.service.dto.DocDTO;
import docpreview.service.mapper.DocMapper;

/**
 * Service Implementation for managing {@link Doc}.
 */
@Service
@Transactional
public class DocServiceImpl implements DocService {

	private final Logger log = LoggerFactory.getLogger(DocServiceImpl.class);

	private final DocRepository docRepository;

	private final DocMapper docMapper;

	private final PDFToImageUtil pdfToImageUtil;

	private FilesystemServiceImpl filesystemServiceImpl;

	@NotNull(message = "pdftoimage.img.dpi can not be null")
	@Value("${pdftoimage.img.dpi}")
	private int imgDpi;

	@NotNull(message = "pdftoimage.img.quality can not be null")
	@Value("${pdftoimage.img.quality}")
	private float imgQuality;

	public DocServiceImpl(DocRepository docRepository, DocMapper docMapper, 
			FilesystemServiceImpl filesystemServiceImpl, PDFToImageUtil pdfToImageUtil) {
		this.docRepository = docRepository;
		this.docMapper = docMapper;
		this.pdfToImageUtil = pdfToImageUtil;
		this.filesystemServiceImpl = filesystemServiceImpl;
	}

	@PostConstruct
	public void postConstruct() throws IOException {
		filesystemServiceImpl.mkdir(filesystemServiceImpl.getDocRootPathPrefix("doc"));
	}

	/**
	 * Save a doc.
	 *
	 * @param docDTO the entity to save.
	 * @return the persisted entity.
	 */
	@Override
	public DocDTO save(DocDTO docDTO) {
		log.debug("Request to save Doc : {}", docDTO);
		Doc doc = docMapper.toEntity(docDTO);

		Instant now = Instant.now();
		if (doc.getId() == null) {
			doc.setCreatedAt(now);
		}
		doc.setUpdatedAt(now);

		doc.setContentSha1(SHAUtil.hash(doc.getContent()));

		doc.setNumberOfPages(null);

		doc = docRepository.save(doc);

		Long id = doc.getId();

		PDDocument pddocument = null;
		try {
			final String docContentContentType = docDTO.getContentContentType();

			if (PDFToImageUtil.MIME_PDF.equals(docContentContentType)) {
				String password = "";
				try {
					pddocument = PDDocument.load(docDTO.getContent(), password);

					doc.setNumberOfPages(pddocument.getNumberOfPages());

					filesystemServiceImpl.deldir(filesystemServiceImpl.getDocPathPrefix(Doc.ENTITY_NAME,id));
					filesystemServiceImpl.mkdir(filesystemServiceImpl.getDocPathPrefix(Doc.ENTITY_NAME,id));
					String imgPrefix = filesystemServiceImpl.getImgPathPrefix(Doc.ENTITY_NAME,id);
					try {
						pdfToImageUtil.converPDFToImage(pddocument, imgPrefix, imgDpi, imgQuality);
					} catch (IllegalArgumentException | IOException e) {
						log.warn("Error of image conversion", e);
					}

				} catch (IOException e) {
					log.warn("can not load PDF document", e);
					doc.setNumberOfPages(null);
					filesystemServiceImpl.deldir(filesystemServiceImpl.getDocPathPrefix(Doc.ENTITY_NAME,id));
				}
			}

			// save numberOfPages
			doc = docRepository.save(doc);
			
		} finally {
			if (pddocument != null) {
				try {
					pddocument.close();
				} catch (IOException e) {
					log.warn("can not close document", e);
					return null;
				}
			}
		}

		return docMapper.toDto(doc);
	}

	/**
	 * Get all the docs.
	 *
	 * @param pageable the pagination information.
	 * @return the list of entities.
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<DocDTO> findAll(Pageable pageable) {
		log.debug("Request to get all Docs");
		return docRepository.findAll(pageable).map(docMapper::toDto);
	}

	/**
	 * Get one doc by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DocDTO> findOne(Long id) {
		log.debug("Request to get Doc : {}", id);
		return docRepository.findById(id).map(docMapper::toDto);
	}

	/**
	 * Delete the doc by id.
	 *
	 * @param id the id of the entity.
	 */
	@Override
	public void delete(Long id) {
		log.debug("Request to delete Doc : {}", id);
		docRepository.deleteById(id);
		filesystemServiceImpl.deldir(filesystemServiceImpl.getDocPathPrefix(Doc.ENTITY_NAME,id));

	}
}
