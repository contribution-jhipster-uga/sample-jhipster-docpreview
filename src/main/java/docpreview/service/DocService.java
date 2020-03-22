package docpreview.service;

import docpreview.service.dto.DocDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link docpreview.domain.Doc}.
 */
public interface DocService {

    /**
     * Save a doc.
     *
     * @param docDTO the entity to save.
     * @return the persisted entity.
     */
    DocDTO save(DocDTO docDTO);

    /**
     * Get all the docs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DocDTO> findAll(Pageable pageable);

    /**
     * Get the "id" doc.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DocDTO> findOne(Long id);

    /**
     * Delete the "id" doc.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
    
}
