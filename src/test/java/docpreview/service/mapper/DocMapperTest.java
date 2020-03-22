package docpreview.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class DocMapperTest {

    private DocMapper docMapper;

    @BeforeEach
    public void setUp() {
        docMapper = new DocMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(docMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(docMapper.fromId(null)).isNull();
    }
}
