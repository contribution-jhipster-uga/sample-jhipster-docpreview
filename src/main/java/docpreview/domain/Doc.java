package docpreview.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;
import java.time.Instant;

/**
 * Entity Doc
 */
@ApiModel(description = "Entity Doc")
@Entity
@Table(name = "doc")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Doc implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * Title du document
     */
    @NotNull
    @Size(min = 2)
    @ApiModelProperty(value = "Title du document", required = true)
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Language of the document (iso code).
     */
    @ApiModelProperty(value = "Language of the document (iso code).")
    @Column(name = "language")
    private String language;

    /**
     * Description of the document
     */
    @Size(max = 2000)
    @ApiModelProperty(value = "Description of the document")
    @Column(name = "description", length = 2000)
    private String description;

    /**
     * Content of the document
     */
    
    @ApiModelProperty(value = "Content of the document", required = true)
    @Lob
    @Column(name = "content", nullable = false)
    private byte[] content;

    @Column(name = "content_content_type", nullable = false)
    private String contentContentType;

    @Size(min = 40, max = 40)
    @Pattern(regexp = "[a-f0-9]{40}")
    @Column(name = "content_sha_1", length = 40)
    private String contentSha1;

    /**
     * Number of pages of the document
     */
    @ApiModelProperty(value = "Number of pages of the document")
    @Column(name = "number_of_pages")
    private Integer numberOfPages;

    /**
     * Creation date
     */
    @NotNull
    @ApiModelProperty(value = "Creation date", required = true)
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * Update date
     */
    @ApiModelProperty(value = "Update date")
    @Column(name = "updated_at")
    private Instant updatedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Doc title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public Doc language(String language) {
        this.language = language;
        return this;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public Doc description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getContent() {
        return content;
    }

    public Doc content(byte[] content) {
        this.content = content;
        return this;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentContentType() {
        return contentContentType;
    }

    public Doc contentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
        return this;
    }

    public void setContentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
    }

    public String getContentSha1() {
        return contentSha1;
    }

    public Doc contentSha1(String contentSha1) {
        this.contentSha1 = contentSha1;
        return this;
    }

    public void setContentSha1(String contentSha1) {
        this.contentSha1 = contentSha1;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public Doc numberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
        return this;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Doc createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Doc updatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Doc)) {
            return false;
        }
        return id != null && id.equals(((Doc) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Doc{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", language='" + getLanguage() + "'" +
            ", description='" + getDescription() + "'" +
            ", content='" + getContent() + "'" +
            ", contentContentType='" + getContentContentType() + "'" +
            ", contentSha1='" + getContentSha1() + "'" +
            ", numberOfPages=" + getNumberOfPages() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
