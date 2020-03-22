package docpreview.web.rest;

import docpreview.DocpreviewApp;
import docpreview.domain.Doc;
import docpreview.repository.DocRepository;
import docpreview.service.DocService;
import docpreview.service.dto.DocDTO;
import docpreview.service.mapper.DocMapper;
import docpreview.service.dto.DocCriteria;
import docpreview.service.DocQueryService;

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
    private static final Integer SMALLER_NUMBER_OF_PAGES = 1 - 1;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private DocRepository docRepository;

    @Autowired
    private DocMapper docMapper;

    @Autowired
    private DocService docService;

    @Autowired
    private DocQueryService docQueryService;

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
        DocDTO docDTO = docMapper.toDto(doc);
        restDocMockMvc.perform(post("/api/docs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(docDTO)))
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
        DocDTO docDTO = docMapper.toDto(doc);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDocMockMvc.perform(post("/api/docs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(docDTO)))
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
        DocDTO docDTO = docMapper.toDto(doc);

        restDocMockMvc.perform(post("/api/docs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(docDTO)))
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
        DocDTO docDTO = docMapper.toDto(doc);

        restDocMockMvc.perform(post("/api/docs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(docDTO)))
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
    public void getDocsByIdFiltering() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        Long id = doc.getId();

        defaultDocShouldBeFound("id.equals=" + id);
        defaultDocShouldNotBeFound("id.notEquals=" + id);

        defaultDocShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDocShouldNotBeFound("id.greaterThan=" + id);

        defaultDocShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDocShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllDocsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where title equals to DEFAULT_TITLE
        defaultDocShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the docList where title equals to UPDATED_TITLE
        defaultDocShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllDocsByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where title not equals to DEFAULT_TITLE
        defaultDocShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the docList where title not equals to UPDATED_TITLE
        defaultDocShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllDocsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultDocShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the docList where title equals to UPDATED_TITLE
        defaultDocShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllDocsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where title is not null
        defaultDocShouldBeFound("title.specified=true");

        // Get all the docList where title is null
        defaultDocShouldNotBeFound("title.specified=false");
    }
                @Test
    @Transactional
    public void getAllDocsByTitleContainsSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where title contains DEFAULT_TITLE
        defaultDocShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the docList where title contains UPDATED_TITLE
        defaultDocShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllDocsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where title does not contain DEFAULT_TITLE
        defaultDocShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the docList where title does not contain UPDATED_TITLE
        defaultDocShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }


    @Test
    @Transactional
    public void getAllDocsByLanguageIsEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where language equals to DEFAULT_LANGUAGE
        defaultDocShouldBeFound("language.equals=" + DEFAULT_LANGUAGE);

        // Get all the docList where language equals to UPDATED_LANGUAGE
        defaultDocShouldNotBeFound("language.equals=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    public void getAllDocsByLanguageIsNotEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where language not equals to DEFAULT_LANGUAGE
        defaultDocShouldNotBeFound("language.notEquals=" + DEFAULT_LANGUAGE);

        // Get all the docList where language not equals to UPDATED_LANGUAGE
        defaultDocShouldBeFound("language.notEquals=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    public void getAllDocsByLanguageIsInShouldWork() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where language in DEFAULT_LANGUAGE or UPDATED_LANGUAGE
        defaultDocShouldBeFound("language.in=" + DEFAULT_LANGUAGE + "," + UPDATED_LANGUAGE);

        // Get all the docList where language equals to UPDATED_LANGUAGE
        defaultDocShouldNotBeFound("language.in=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    public void getAllDocsByLanguageIsNullOrNotNull() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where language is not null
        defaultDocShouldBeFound("language.specified=true");

        // Get all the docList where language is null
        defaultDocShouldNotBeFound("language.specified=false");
    }
                @Test
    @Transactional
    public void getAllDocsByLanguageContainsSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where language contains DEFAULT_LANGUAGE
        defaultDocShouldBeFound("language.contains=" + DEFAULT_LANGUAGE);

        // Get all the docList where language contains UPDATED_LANGUAGE
        defaultDocShouldNotBeFound("language.contains=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    public void getAllDocsByLanguageNotContainsSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where language does not contain DEFAULT_LANGUAGE
        defaultDocShouldNotBeFound("language.doesNotContain=" + DEFAULT_LANGUAGE);

        // Get all the docList where language does not contain UPDATED_LANGUAGE
        defaultDocShouldBeFound("language.doesNotContain=" + UPDATED_LANGUAGE);
    }


    @Test
    @Transactional
    public void getAllDocsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where description equals to DEFAULT_DESCRIPTION
        defaultDocShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the docList where description equals to UPDATED_DESCRIPTION
        defaultDocShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllDocsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where description not equals to DEFAULT_DESCRIPTION
        defaultDocShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the docList where description not equals to UPDATED_DESCRIPTION
        defaultDocShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllDocsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultDocShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the docList where description equals to UPDATED_DESCRIPTION
        defaultDocShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllDocsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where description is not null
        defaultDocShouldBeFound("description.specified=true");

        // Get all the docList where description is null
        defaultDocShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllDocsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where description contains DEFAULT_DESCRIPTION
        defaultDocShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the docList where description contains UPDATED_DESCRIPTION
        defaultDocShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllDocsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where description does not contain DEFAULT_DESCRIPTION
        defaultDocShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the docList where description does not contain UPDATED_DESCRIPTION
        defaultDocShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllDocsByContentSha1IsEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where contentSha1 equals to DEFAULT_CONTENT_SHA_1
        defaultDocShouldBeFound("contentSha1.equals=" + DEFAULT_CONTENT_SHA_1);

        // Get all the docList where contentSha1 equals to UPDATED_CONTENT_SHA_1
        defaultDocShouldNotBeFound("contentSha1.equals=" + UPDATED_CONTENT_SHA_1);
    }

    @Test
    @Transactional
    public void getAllDocsByContentSha1IsNotEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where contentSha1 not equals to DEFAULT_CONTENT_SHA_1
        defaultDocShouldNotBeFound("contentSha1.notEquals=" + DEFAULT_CONTENT_SHA_1);

        // Get all the docList where contentSha1 not equals to UPDATED_CONTENT_SHA_1
        defaultDocShouldBeFound("contentSha1.notEquals=" + UPDATED_CONTENT_SHA_1);
    }

    @Test
    @Transactional
    public void getAllDocsByContentSha1IsInShouldWork() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where contentSha1 in DEFAULT_CONTENT_SHA_1 or UPDATED_CONTENT_SHA_1
        defaultDocShouldBeFound("contentSha1.in=" + DEFAULT_CONTENT_SHA_1 + "," + UPDATED_CONTENT_SHA_1);

        // Get all the docList where contentSha1 equals to UPDATED_CONTENT_SHA_1
        defaultDocShouldNotBeFound("contentSha1.in=" + UPDATED_CONTENT_SHA_1);
    }

    @Test
    @Transactional
    public void getAllDocsByContentSha1IsNullOrNotNull() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where contentSha1 is not null
        defaultDocShouldBeFound("contentSha1.specified=true");

        // Get all the docList where contentSha1 is null
        defaultDocShouldNotBeFound("contentSha1.specified=false");
    }
                @Test
    @Transactional
    public void getAllDocsByContentSha1ContainsSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where contentSha1 contains DEFAULT_CONTENT_SHA_1
        defaultDocShouldBeFound("contentSha1.contains=" + DEFAULT_CONTENT_SHA_1);

        // Get all the docList where contentSha1 contains UPDATED_CONTENT_SHA_1
        defaultDocShouldNotBeFound("contentSha1.contains=" + UPDATED_CONTENT_SHA_1);
    }

    @Test
    @Transactional
    public void getAllDocsByContentSha1NotContainsSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where contentSha1 does not contain DEFAULT_CONTENT_SHA_1
        defaultDocShouldNotBeFound("contentSha1.doesNotContain=" + DEFAULT_CONTENT_SHA_1);

        // Get all the docList where contentSha1 does not contain UPDATED_CONTENT_SHA_1
        defaultDocShouldBeFound("contentSha1.doesNotContain=" + UPDATED_CONTENT_SHA_1);
    }


    @Test
    @Transactional
    public void getAllDocsByNumberOfPagesIsEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where numberOfPages equals to DEFAULT_NUMBER_OF_PAGES
        defaultDocShouldBeFound("numberOfPages.equals=" + DEFAULT_NUMBER_OF_PAGES);

        // Get all the docList where numberOfPages equals to UPDATED_NUMBER_OF_PAGES
        defaultDocShouldNotBeFound("numberOfPages.equals=" + UPDATED_NUMBER_OF_PAGES);
    }

    @Test
    @Transactional
    public void getAllDocsByNumberOfPagesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where numberOfPages not equals to DEFAULT_NUMBER_OF_PAGES
        defaultDocShouldNotBeFound("numberOfPages.notEquals=" + DEFAULT_NUMBER_OF_PAGES);

        // Get all the docList where numberOfPages not equals to UPDATED_NUMBER_OF_PAGES
        defaultDocShouldBeFound("numberOfPages.notEquals=" + UPDATED_NUMBER_OF_PAGES);
    }

    @Test
    @Transactional
    public void getAllDocsByNumberOfPagesIsInShouldWork() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where numberOfPages in DEFAULT_NUMBER_OF_PAGES or UPDATED_NUMBER_OF_PAGES
        defaultDocShouldBeFound("numberOfPages.in=" + DEFAULT_NUMBER_OF_PAGES + "," + UPDATED_NUMBER_OF_PAGES);

        // Get all the docList where numberOfPages equals to UPDATED_NUMBER_OF_PAGES
        defaultDocShouldNotBeFound("numberOfPages.in=" + UPDATED_NUMBER_OF_PAGES);
    }

    @Test
    @Transactional
    public void getAllDocsByNumberOfPagesIsNullOrNotNull() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where numberOfPages is not null
        defaultDocShouldBeFound("numberOfPages.specified=true");

        // Get all the docList where numberOfPages is null
        defaultDocShouldNotBeFound("numberOfPages.specified=false");
    }

    @Test
    @Transactional
    public void getAllDocsByNumberOfPagesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where numberOfPages is greater than or equal to DEFAULT_NUMBER_OF_PAGES
        defaultDocShouldBeFound("numberOfPages.greaterThanOrEqual=" + DEFAULT_NUMBER_OF_PAGES);

        // Get all the docList where numberOfPages is greater than or equal to UPDATED_NUMBER_OF_PAGES
        defaultDocShouldNotBeFound("numberOfPages.greaterThanOrEqual=" + UPDATED_NUMBER_OF_PAGES);
    }

    @Test
    @Transactional
    public void getAllDocsByNumberOfPagesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where numberOfPages is less than or equal to DEFAULT_NUMBER_OF_PAGES
        defaultDocShouldBeFound("numberOfPages.lessThanOrEqual=" + DEFAULT_NUMBER_OF_PAGES);

        // Get all the docList where numberOfPages is less than or equal to SMALLER_NUMBER_OF_PAGES
        defaultDocShouldNotBeFound("numberOfPages.lessThanOrEqual=" + SMALLER_NUMBER_OF_PAGES);
    }

    @Test
    @Transactional
    public void getAllDocsByNumberOfPagesIsLessThanSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where numberOfPages is less than DEFAULT_NUMBER_OF_PAGES
        defaultDocShouldNotBeFound("numberOfPages.lessThan=" + DEFAULT_NUMBER_OF_PAGES);

        // Get all the docList where numberOfPages is less than UPDATED_NUMBER_OF_PAGES
        defaultDocShouldBeFound("numberOfPages.lessThan=" + UPDATED_NUMBER_OF_PAGES);
    }

    @Test
    @Transactional
    public void getAllDocsByNumberOfPagesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where numberOfPages is greater than DEFAULT_NUMBER_OF_PAGES
        defaultDocShouldNotBeFound("numberOfPages.greaterThan=" + DEFAULT_NUMBER_OF_PAGES);

        // Get all the docList where numberOfPages is greater than SMALLER_NUMBER_OF_PAGES
        defaultDocShouldBeFound("numberOfPages.greaterThan=" + SMALLER_NUMBER_OF_PAGES);
    }


    @Test
    @Transactional
    public void getAllDocsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where createdAt equals to DEFAULT_CREATED_AT
        defaultDocShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the docList where createdAt equals to UPDATED_CREATED_AT
        defaultDocShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    public void getAllDocsByCreatedAtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where createdAt not equals to DEFAULT_CREATED_AT
        defaultDocShouldNotBeFound("createdAt.notEquals=" + DEFAULT_CREATED_AT);

        // Get all the docList where createdAt not equals to UPDATED_CREATED_AT
        defaultDocShouldBeFound("createdAt.notEquals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    public void getAllDocsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultDocShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the docList where createdAt equals to UPDATED_CREATED_AT
        defaultDocShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    public void getAllDocsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where createdAt is not null
        defaultDocShouldBeFound("createdAt.specified=true");

        // Get all the docList where createdAt is null
        defaultDocShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    public void getAllDocsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultDocShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the docList where updatedAt equals to UPDATED_UPDATED_AT
        defaultDocShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    public void getAllDocsByUpdatedAtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where updatedAt not equals to DEFAULT_UPDATED_AT
        defaultDocShouldNotBeFound("updatedAt.notEquals=" + DEFAULT_UPDATED_AT);

        // Get all the docList where updatedAt not equals to UPDATED_UPDATED_AT
        defaultDocShouldBeFound("updatedAt.notEquals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    public void getAllDocsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultDocShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the docList where updatedAt equals to UPDATED_UPDATED_AT
        defaultDocShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    public void getAllDocsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docList where updatedAt is not null
        defaultDocShouldBeFound("updatedAt.specified=true");

        // Get all the docList where updatedAt is null
        defaultDocShouldNotBeFound("updatedAt.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDocShouldBeFound(String filter) throws Exception {
        restDocMockMvc.perform(get("/api/docs?sort=id,desc&" + filter))
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

        // Check, that the count call also returns 1
        restDocMockMvc.perform(get("/api/docs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDocShouldNotBeFound(String filter) throws Exception {
        restDocMockMvc.perform(get("/api/docs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDocMockMvc.perform(get("/api/docs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
        DocDTO docDTO = docMapper.toDto(updatedDoc);

        restDocMockMvc.perform(put("/api/docs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(docDTO)))
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
        DocDTO docDTO = docMapper.toDto(doc);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocMockMvc.perform(put("/api/docs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(docDTO)))
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
