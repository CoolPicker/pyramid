package com.rosetta.image;

import com.rosetta.image.service.IImageService;
import com.rosetta.image.service.impl.HashServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PyramidApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(PyramidApplicationTests.class);

	@Autowired
	private IImageService imageService;

	@Autowired
	private HashServiceImpl hashService;

	@Test
	public void batchUpdateIdf(){
		hashService.updateIdf();
	}

	@Test
	public void contextLoads() {
		File file = new File("/home/han/image-retrieval/image.process.log");
		BufferedReader reader = null;
		String tempString = null;
		int line  = 1;
		try {
			reader = new BufferedReader(new FileReader(file));

			while (StringUtils.isNotBlank(tempString = reader.readLine())){
                if (line >= 1816) {
					System.out.println("line : " + line + " : " + tempString);
                    imageService.detectImagePath(tempString);
                }
				//imageService.detectImagePath(tempString);
				line++;
			}
			reader.close();
		} catch (Exception e) {
			log.error("read line error with line : " + line+  " the url " + tempString  , e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error("reader io then close error : " , e);
				}
			}
		}
	}

}
