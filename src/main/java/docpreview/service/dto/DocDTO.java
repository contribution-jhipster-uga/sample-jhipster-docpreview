package docpreview.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the {@link docpreview.domain.Doc} entity.
 */
@ApiModel(description = "Entity Doc")
public class DocDTO implements Serializable {
    
    private Long id;

    /**
     * Title du document
     */
    @NotNull
    @Size(min = 2)
    @ApiModelProperty(value = "Title du document", required = true)
    private String title;

    /**
     * Language of the document (ISO code).
     */
    @ApiModelProperty(value = "Language of the document (ISO code).")
    private String language;

    /**
     * Description of the document
     */
    @Size(max = 2000)
    @ApiModelProperty(value = "Description of the document")
    private String description;

    /**
     * Content of the document
     */  
    @ApiModelProperty(value = "Content of the document", required = true)
    @Lob
    private byte[] content;

    private String contentContentType;
    @Pattern(regexp = "$([a-f0-9]{40}$)?$")
    private String contentSha1;

    /**
     * Number of pages of the document
     */
    @ApiModelProperty(value = "Number of pages of the document")
    private Integer numberOfPages;

    /**
     * Creation date
     */
    // @NotNull
    @ApiModelProperty(value = "Creation date", required = true)
    private Instant createdAt;

    /**
     * Update date
     */
    @ApiModelProperty(value = "Update date")
    private Instant updatedAt;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentContentType() {
        return contentContentType;
    }

    public void setContentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
    }

    public String getContentSha1() {
        return contentSha1;
    }

    public void setContentSha1(String contentSha1) {
        this.contentSha1 = contentSha1;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DocDTO docDTO = (DocDTO) o;
        if (docDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), docDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DocDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", language='" + getLanguage() + "'" +
            ", description='" + getDescription() + "'" +
            ", content='" + getContent() + "'" +
            ", contentSha1='" + getContentSha1() + "'" +
            ", numberOfPages=" + getNumberOfPages() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
