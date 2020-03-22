package docpreview.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link docpreview.domain.Doc} entity. This class is used
 * in {@link docpreview.web.rest.DocResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /docs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class DocCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter language;

    private StringFilter description;

    private StringFilter contentSha1;

    private IntegerFilter numberOfPages;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    public DocCriteria() {
    }

    public DocCriteria(DocCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.language = other.language == null ? null : other.language.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.contentSha1 = other.contentSha1 == null ? null : other.contentSha1.copy();
        this.numberOfPages = other.numberOfPages == null ? null : other.numberOfPages.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.updatedAt = other.updatedAt == null ? null : other.updatedAt.copy();
    }

    @Override
    public DocCriteria copy() {
        return new DocCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getLanguage() {
        return language;
    }

    public void setLanguage(StringFilter language) {
        this.language = language;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getContentSha1() {
        return contentSha1;
    }

    public void setContentSha1(StringFilter contentSha1) {
        this.contentSha1 = contentSha1;
    }

    public IntegerFilter getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(IntegerFilter numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
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
        final DocCriteria that = (DocCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(language, that.language) &&
            Objects.equals(description, that.description) &&
            Objects.equals(contentSha1, that.contentSha1) &&
            Objects.equals(numberOfPages, that.numberOfPages) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        title,
        language,
        description,
        contentSha1,
        numberOfPages,
        createdAt,
        updatedAt
        );
    }

    @Override
    public String toString() {
        return "DocCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (title != null ? "title=" + title + ", " : "") +
                (language != null ? "language=" + language + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (contentSha1 != null ? "contentSha1=" + contentSha1 + ", " : "") +
                (numberOfPages != null ? "numberOfPages=" + numberOfPages + ", " : "") +
                (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
                (updatedAt != null ? "updatedAt=" + updatedAt + ", " : "") +
            "}";
    }

}
