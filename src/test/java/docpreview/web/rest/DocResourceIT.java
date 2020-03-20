package docpreview.web.rest;

import docpreview.DocpreviewApp;
import docpreview.domain.Doc;
import docpreview.repository.DocRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link DocResource} REST controller.
 */
@SpringBootTest(classes = DocpreviewApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class DocResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_LANGUAGE = "AAAAAAAAAA";
    private static final String UPDATED_LANGUAGE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_CONTENT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_CONTENT = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_CONTENT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_CONTENT_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_CONTENT_SHA_1 = "6f7a06c0dd8059aaa2c9d3a8497409d54ab3ac11";
    private static final String UPDATED_CONTENT_SHA_1 = "23d0d6a6f858e60c8e297ed38fef8601cb9ceb75";

    private static final Integer DEFAULT_NUMBER_OF_PAGES = 1;
    private static final Integer UPDATED_NUMBER_OF_PAGES = 2;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private DocRepository docRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDocMockMvc;

    private Doc doc;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Doc createEntity(EntityManager em) {
        Doc doc = new Doc()
            .title(DEFAULT_TITLE)
            .language(DEFAULT_LANGUAGE)
            .description(DEFAULT_DESCRIPTION)
            .content(DEFAULT_CONTENT)
            .contentContentType(DEFAULT_CONTENT_CONTENT_TYPE)
            .contentSha1(DEFAULT_CONTENT_SHA_1)
            .numberOfPages(DEFAULT_NUMBER_OF_PAGES)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return doc;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Doc createUpdatedEntity(EntityManager em) {
        Doc doc = new Doc()
            .title(UPDATED_TITLE)
            .language(UPDATED_LANGUAGE)
            .description(UPDATED_DESCRIPTION)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE)
            .contentSha1(UPDATED_CONTENT_SHA_1)
            .numberOfPages(UPDATED_NUMBER_OF_PAGES)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        return doc;
    }

    @BeforeEach
    public void initTest() {
        doc = createEntity(em);
    }

    @Test
    @Transactional
    public void createDoc() throws Exception {
        int databaseSizeBeforeCreate = docRepository.findAll().size();

        // Create the Doc
        restDocMockMvc.perform(post("/api/docs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(doc)))
            .andExpect(status().isCreated());

        // Validate the Doc in the database
        List<Doc> docList = docRepository.findAll();
        assertThat(docList).hasSize(databaseSizeBeforeCreate + 1);
        Doc testDoc = docList.get(docList.size() - 1);
        assertThat(testDoc.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testDoc.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
        assertThat(testDoc.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testDoc.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testDoc.getContentContentType()).isEqualTo(DEFAULT_CONTENT_CONTENT_TYPE);
        assertThat(testDoc.getContentSha1()).isEqualTo(DEFAULT_CONTENT_SHA_1);
        assertThat(testDoc.getNumberOfPages()).isEqualTo(DEFAULT_NUMBER_OF_PAGES);
        assertThat(testDoc.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testDoc.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    public void createDocWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = docRepository.findAll().size();

        // Create the Doc with an existing ID
        doc.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDocMockMvc.perform(post("/api/docs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(doc)))
            .andExpect(status().isBadRequest());

        // Validate the Doc in the database
        List<Doc> docList = docRepository.findAll();
        assertThat(docList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = docRepository.findAll().size();
        // set the field null
        doc.setTitle(null);

        // Create the Doc, which fails.

        restDocMockMvc.perform(post("/api/docs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(doc)))
            .andExpect(status().isBadRequest());

        List<Doc> docList = docRepository.findAll();
        assertThat(docList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = docRepository.findAll().size();
        // set the field null
        doc.setCreatedAt(null);

        // Create the Doc, which fails.

        restDocMockMvc.perform(post("/api/docs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(doc)))
            .andExpect(status().isBadRequest());

        List<Doc> docList = docRepository.findAll();
        assertThat(docList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDocs() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList
        restDocMockMvc.perform(get("/api/docs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doc.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].contentContentType").value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(Base64Utils.encodeToString(DEFAULT_CONTENT))))
            .andExpect(jsonPath("$.[*].contentSha1").value(hasItem(DEFAULT_CONTENT_SHA_1)))
            .andExpect(jsonPath("$.[*].numberOfPages").value(hasItem(DEFAULT_NUMBER_OF_PAGES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }
    
    @Test
    @Transactional
    public void getDoc() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get the doc
        restDocMockMvc.perform(get("/api/docs/{id}", doc.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(doc.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.contentContentType").value(DEFAULT_CONTENT_CONTENT_TYPE))
            .andExpect(jsonPath("$.content").value(Base64Utils.encodeToString(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.contentSha1").value(DEFAULT_CONTENT_SHA_1))
            .andExpect(jsonPath("$.numberOfPages").value(DEFAULT_NUMBER_OF_PAGES))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDoc() throws Exception {
        // Get the doc
        restDocMockMvc.perform(get("/api/docs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDoc() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        int databaseSizeBeforeUpdate = docRepository.findAll().size();

        // Update the doc
        Doc updatedDoc = docRepository.findById(doc.getId()).get();
        // Disconnect from session so that the updates on updatedDoc are not directly saved in db
        em.detach(updatedDoc);
        updatedDoc
            .title(UPDATED_TITLE)
            .language(UPDATED_LANGUAGE)
            .description(UPDATED_DESCRIPTION)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE)
            .contentSha1(UPDATED_CONTENT_SHA_1)
            .numberOfPages(UPDATED_NUMBER_OF_PAGES)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restDocMockMvc.perform(put("/api/docs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedDoc)))
            .andExpect(status().isOk());

        // Validate the Doc in the database
        List<Doc> docList = docRepository.findAll();
        assertThat(docList).hasSize(databaseSizeBeforeUpdate);
        Doc testDoc = docList.get(docList.size() - 1);
        assertThat(testDoc.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testDoc.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
        assertThat(testDoc.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testDoc.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testDoc.getContentContentType()).isEqualTo(UPDATED_CONTENT_CONTENT_TYPE);
        assertThat(testDoc.getContentSha1()).isEqualTo(UPDATED_CONTENT_SHA_1);
        assertThat(testDoc.getNumberOfPages()).isEqualTo(UPDATED_NUMBER_OF_PAGES);
        assertThat(testDoc.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testDoc.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    public void updateNonExistingDoc() throws Exception {
        int databaseSizeBeforeUpdate = docRepository.findAll().size();

        // Create the Doc

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocMockMvc.perform(put("/api/docs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(doc)))
            .andExpect(status().isBadRequest());

        // Validate the Doc in the database
        List<Doc> docList = docRepository.findAll();
        assertThat(docList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteDoc() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        int databaseSizeBeforeDelete = docRepository.findAll().size();

        // Delete the doc
        restDocMockMvc.perform(delete("/api/docs/{id}", doc.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Doc> docList = docRepository.findAll();
        assertThat(docList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
