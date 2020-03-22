package docpreview.web.rest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import docpreview.domain.Doc;
import docpreview.pdfbox.tools.MimeTypes;
import docpreview.pdfbox.tools.SHAUtil;
import docpreview.service.DocQueryService;
import docpreview.service.DocService;
import docpreview.service.dto.DocCriteria;
import docpreview.service.dto.DocDTO;
import docpreview.service.impl.FilesystemServiceImpl;
import docpreview.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import io.micrometer.core.annotation.Timed;

/**
 * REST controller for managing {@link docpreview.domain.Doc}.
 */
@RestController
@RequestMapping("/api")
public class DocResource {

	private final Logger log = LoggerFactory.getLogger(DocResource.class);

	@Value("${jhipster.clientApp.name}")
	private String applicationName;

	private final DocService docService;

	private final DocQueryService docQueryService;

	private FilesystemServiceImpl filesystemServiceImpl;

	public DocResource(DocService docService, DocQueryService docQueryService,
			FilesystemServiceImpl filesystemServiceImpl) {
		this.docService = docService;
		this.docQueryService = docQueryService;
		this.filesystemServiceImpl = filesystemServiceImpl;

	}

	/**
	 * {@code POST  /docs} : Create a new doc.
	 *
	 * @param docDTO the docDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new docDTO, or with status {@code 400 (Bad Request)} if the
	 *         doc has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/docs")
	public ResponseEntity<DocDTO> createDoc(@Valid @RequestBody DocDTO docDTO) throws URISyntaxException {
		log.debug("REST request to save Doc : {}", docDTO);
		if (docDTO.getId() != null) {
			throw new BadRequestAlertException("A new doc cannot already have an ID", Doc.ENTITY_NAME, "idexists");
		}
		DocDTO result = docService.save(docDTO);
		return ResponseEntity
				.created(new URI("/api/docs/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, Doc.ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /docs} : Updates an existing doc.
	 *
	 * @param docDTO the docDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated docDTO, or with status {@code 400 (Bad Request)} if the
	 *         docDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the docDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping("/docs")
	public ResponseEntity<DocDTO> updateDoc(@Valid @RequestBody DocDTO docDTO) throws URISyntaxException {
		log.debug("REST request to update Doc : {}", docDTO);
		if (docDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", Doc.ENTITY_NAME, "idnull");
		}
		DocDTO result = docService.save(docDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, Doc.ENTITY_NAME, docDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /docs} : get all the docs.
	 *
	 * @param pageable the pagination information.
	 * @param criteria the criteria which the requested entities should match.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of docs in body.
	 */
	@GetMapping("/docs")
	public ResponseEntity<List<DocDTO>> getAllDocs(DocCriteria criteria, Pageable pageable) {
		log.debug("REST request to get Docs by criteria: {}", criteria);
		Page<DocDTO> page = docQueryService.findByCriteria(criteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers).body(page.getContent());
	}

	/**
	 * {@code GET  /docs/count} : count all the docs.
	 *
	 * @param criteria the criteria which the requested entities should match.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
	 *         in body.
	 */
	@GetMapping("/docs/count")
	public ResponseEntity<Long> countDocs(DocCriteria criteria) {
		log.debug("REST request to count Docs by criteria: {}", criteria);
		return ResponseEntity.ok().body(docQueryService.countByCriteria(criteria));
	}

	/**
	 * {@code GET  /docs/:id} : get the "id" doc.
	 *
	 * @param id the id of the docDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the docDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/docs/{id}")
	public ResponseEntity<DocDTO> getDoc(@PathVariable Long id) {
		log.debug("REST request to get Doc : {}", id);
		Optional<DocDTO> docDTO = docService.findOne(id);
		return ResponseUtil.wrapOrNotFound(docDTO);
	}

	/**
	 * {@code DELETE  /docs/:id} : delete the "id" doc.
	 *
	 * @param id the id of the docDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@DeleteMapping("/docs/{id}")
	public ResponseEntity<Void> deleteDoc(@PathVariable Long id) {
		log.debug("REST request to delete Doc : {}", id);
		docService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, Doc.ENTITY_NAME, id.toString()))
				.build();
	}

	@GetMapping(value = "/docs/{id}/content")
	@Timed
	public ResponseEntity<byte[]> getContentAsResponseEntity(
			@RequestHeader(value = HttpHeaders.IF_NONE_MATCH, defaultValue = "") final String ifNoneMatch,
			@PathVariable final Long id) {
		log.debug("REST request from {} to get the contenu of Doc : {}", id);
		final Optional<DocDTO> docDTO = docService.findOne(id);
		if (docDTO.isPresent()) {
			final DocDTO d = docDTO.get();

			final byte[] buf = d.getContent();
			if (buf != null) {
				final String contentType = d.getContentContentType();
				final String sha1 = d.getContentSha1();
				final String name = Doc.ENTITY_NAME + "-" + id + "." + MimeTypes.lookupExt(contentType);
				return getResponseEntity(buf, sha1, contentType, d.getUpdatedAt(), ifNoneMatch, name);
			}
		}
		return ResponseUtil.wrapOrNotFound(Optional.ofNullable(null));

	}

	@GetMapping(value = "/docs/{id}/img/{page}", produces = { MediaType.IMAGE_JPEG_VALUE })
	@Timed
	public ResponseEntity<byte[]> getImgAsResponseEntity(
			@RequestHeader(value = HttpHeaders.IF_NONE_MATCH, defaultValue = "") final String ifNoneMatch,
			@PathVariable final Long id, @PathVariable final Long page) {
		log.debug("REST request to get the img of Doc : id={} page={} ifNoneMatch={}", id, page, ifNoneMatch);
		final String filename = Doc.ENTITY_NAME + "-" + id + "-img-" + page + ".jpg";
		return getResponseEntity(id, page, filesystemServiceImpl.getImgPath(Doc.ENTITY_NAME, id, page), ifNoneMatch,
				filename);

	}

	/**
	 * return the byte array of the content of the file
	 * 
	 * @param path the path of the file
	 * @return the byte array of the content of the file
	 */
	private byte[] readFile(final String path) {
		final File f = new File(path);
		if (!f.exists()) {
			return null;
		}
		byte[] buf = null;
		try {
			final Path p = Paths.get(path);
			buf = Files.readAllBytes(p);
			return buf;
		} catch (java.io.IOException e) {
			return null;
		}
	}

	private final String CACHE_CONTROL_NO_CACHE = CacheControl.noCache().getHeaderValue();
	private final long MAXAGE = 60;
	private final String CACHE_CONTROL_MAXAGE = CacheControl.maxAge(MAXAGE, TimeUnit.MINUTES).getHeaderValue();

	private final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	private ResponseEntity<byte[]> getResponseEntity(final byte[] buf, final String sha1, final String contentType,
			final Instant dateModification, final String ifNoneMatch, final String filename) {
		// TODO Select the cacheControl policy according to the subject
		final String cacheControl = CACHE_CONTROL_MAXAGE;

		if (ifNoneMatch.equals("\"" + sha1 + "\"")) {
			return getResponseEntityForNotModified(contentType, dateModification, sha1, cacheControl);
		} else {
			return getResponseEntityForOK(buf, contentType, dateModification, sha1, cacheControl, filename);
		}
	}

	/**
	 * return the ResponseEntity of the page's image of the doc
	 * 
	 * @param id          identifier of the doc
	 * @param page        number of the page
	 * @param path        path of the file to return
	 * @param ifNoneMatch the string of the ETag to match
	 * @return
	 */
	private ResponseEntity<byte[]> getResponseEntity(final Long id, final Long page, final String path,
			final String ifNoneMatch, String filename) {
		final Optional<DocDTO> opdocDTO = docService.findOne(id);
		if (opdocDTO.isPresent()) {
			DocDTO docDTO = opdocDTO.get();
			log.debug("REST request to get the image of Doc id={} page={} path={} ifNoneMatch={}", id, page, path,
					ifNoneMatch);

			final String shaPath = path + SHAUtil.SHA_EXTENSION;

			final String cacheControl = CACHE_CONTROL_MAXAGE;

			final byte[] shaBuf = readFile(shaPath);
			if (shaBuf != null) {
				String shaString = new String(shaBuf, UTF8_CHARSET);
				if (ifNoneMatch.equals("\"" + shaString + "\"")) {
					return getResponseEntityForNotModified(MimeTypeUtils.IMAGE_JPEG_VALUE, docDTO.getUpdatedAt(),
							shaString, cacheControl);
				}
			}

			final byte[] buf = readFile(path);
			if (buf == null) {
				return ResponseUtil.wrapOrNotFound(Optional.ofNullable(null));
			} else {
				final String sha = SHAUtil.hash(buf);
				final File s = new File(shaPath);
				if (!s.exists()) {
					try {
						// crée le fichier s'il n'existe pas
						FileUtils.writeStringToFile(s, sha, "UTF-8");
					} catch (IOException e) {
						log.warn("Can not save SHA1 of the img for doc id={} page={} path={}", id, page, path);
					}
				}
				return getResponseEntityForOK(buf, MimeTypeUtils.IMAGE_JPEG_VALUE, docDTO.getUpdatedAt(), sha,
						cacheControl, filename);
			}
		}
		return ResponseUtil.wrapOrNotFound(Optional.ofNullable(null));
	}

	/**
	 * Helper for byte[] ResponseEntity
	 * 
	 * @param buf
	 * @param contentType
	 * @param updateAt
	 * @param sha1
	 * @return
	 * @TODO add the number of pages or null
	 */
	private ResponseEntity<byte[]> getResponseEntityForOK(final byte[] buf, final String contentType,
			final Instant updateAt, final String sha1, final String cacheControl, final String filename) {
		HttpHeaders headers = new HttpHeaders();

		if (buf != null) {
			headers.setContentLength(buf.length);
		}
		headers.setCacheControl(cacheControl);

		headers.set(HttpHeaders.CONTENT_TYPE, contentType); // TODO ajout du Charset au contentType ? par exemple,
															// application/json;charset=UTF-8
		if (filename != null) {
			headers.setContentDisposition(ContentDisposition.builder("inline").filename(filename).build());
		}

		if (updateAt != null) {
			OffsetDateTime odt = updateAt.atOffset(ZoneOffset.UTC);
			headers.set(HttpHeaders.LAST_MODIFIED, odt.format(DateTimeFormatter.RFC_1123_DATE_TIME));
		}
		if (sha1 != null) {
			headers.setETag("\"" + sha1 + "\"");
		}
		ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(buf, headers, HttpStatus.OK);
		return responseEntity;
	}

	/**
	 * Helper for byte[] ResponseEntity
	 * 
	 * @param buf
	 * @param contentType
	 * @param updateAt
	 * @param sha1
	 * @return
	 * @TODO add the number of pages or null
	 */
	private ResponseEntity<byte[]> getResponseEntityForNotModified(final String contentType, final Instant updateAt,
			final String sha1, final String cacheControl) {
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(cacheControl);
		headers.set(HttpHeaders.CONTENT_TYPE, contentType); // TODO ajout du Charset au contentType ? par exemple,
															// application/json;charset=UTF-8
		if (updateAt != null) {
			OffsetDateTime odt = updateAt.atOffset(ZoneOffset.UTC);
			headers.set(HttpHeaders.LAST_MODIFIED, odt.format(DateTimeFormatter.RFC_1123_DATE_TIME));
		}
		if (sha1 != null) {
			headers.setETag("\"" + sha1 + "\"");
		}
		ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(null, headers, HttpStatus.NOT_MODIFIED);
		return responseEntity;
	}

}
