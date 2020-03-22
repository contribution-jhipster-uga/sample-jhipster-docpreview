package docpreview.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import docpreview.web.rest.TestUtil;

public class DocDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DocDTO.class);
        DocDTO docDTO1 = new DocDTO();
        docDTO1.setId(1L);
        DocDTO docDTO2 = new DocDTO();
        assertThat(docDTO1).isNotEqualTo(docDTO2);
        docDTO2.setId(docDTO1.getId());
        assertThat(docDTO1).isEqualTo(docDTO2);
        docDTO2.setId(2L);
        assertThat(docDTO1).isNotEqualTo(docDTO2);
        docDTO1.setId(null);
        assertThat(docDTO1).isNotEqualTo(docDTO2);
    }
}
