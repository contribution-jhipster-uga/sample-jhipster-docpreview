package docpreview.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import docpreview.domain.Doc;

/**
 * Service Implementation for managing {@link Doc}.
 */
@Service
public class FilesystemServiceImpl {

	private final Logger log = LoggerFactory.getLogger(FilesystemServiceImpl.class);

	@NotNull(message = "filesystem.rootdir can not be null")
	@Value("${filesystem.rootdir}")
	private String fileSystemRootDir;

	@PostConstruct
	public void postConstruct() throws IOException {
		mkdir(fileSystemRootDir);
	}

	public void mkdir(String path) {
		File d = new File(path);
		if (!d.exists()) {
			d.mkdirs();
		}
	}

	public void deldir(String path) {
		deldir(new File(path));
	}

	public void deldir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				if (!Files.isSymbolicLink(f.toPath())) {
					deldir(f);
				}
			}
		}
		file.delete();
	}

	public String getDocRootPathPrefix(String entityName) {
		return fileSystemRootDir + File.separator + entityName;
	}

	public String getDocPathPrefix(String entityName, long id) {
		return getDocRootPathPrefix(entityName) + File.separator + id;
	}

	public String getImgPathPrefix(String entityName, long id) {
		return getDocPathPrefix(entityName, id) + File.separator + "img.";
	}
	
    /**
     * Get the local path to the image file of the page
     * @param id the "id" doc
     * @param page the page number
     * @return the local path to the image file of the page
     */
	public String getImgPath(String entityName, long id, long page) {
		return getImgPathPrefix(entityName, id) + page + ".jpg";
	}
}
