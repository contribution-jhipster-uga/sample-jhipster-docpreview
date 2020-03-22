package docpreview.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import docpreview.domain.Doc;
import docpreview.domain.*; // for static metamodels
import docpreview.repository.DocRepository;
import docpreview.service.dto.DocCriteria;
import docpreview.service.dto.DocDTO;
import docpreview.service.mapper.DocMapper;

/**
 * Service for executing complex queries for {@link Doc} entities in the database.
 * The main input is a {@link DocCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DocDTO} or a {@link Page} of {@link DocDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DocQueryService extends QueryService<Doc> {

    private final Logger log = LoggerFactory.getLogger(DocQueryService.class);

    private final DocRepository docRepository;

    private final DocMapper docMapper;

    public DocQueryService(DocRepository docRepository, DocMapper docMapper) {
        this.docRepository = docRepository;
        this.docMapper = docMapper;
    }

    /**
     * Return a {@link List} of {@link DocDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DocDTO> findByCriteria(DocCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Doc> specification = createSpecification(criteria);
        return docMapper.toDto(docRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DocDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DocDTO> findByCriteria(DocCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Doc> specification = createSpecification(criteria);
        return docRepository.findAll(specification, page)
            .map(docMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DocCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Doc> specification = createSpecification(criteria);
        return docRepository.count(specification);
    }

    /**
     * Function to convert {@link DocCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Doc> createSpecification(DocCriteria criteria) {
        Specification<Doc> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Doc_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Doc_.title));
            }
            if (criteria.getLanguage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLanguage(), Doc_.language));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Doc_.description));
            }
            if (criteria.getContentSha1() != null) {
                specification = specification.and(buildStringSpecification(criteria.getContentSha1(), Doc_.contentSha1));
            }
            if (criteria.getNumberOfPages() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNumberOfPages(), Doc_.numberOfPages));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Doc_.createdAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), Doc_.updatedAt));
            }
        }
        return specification;
    }
}
