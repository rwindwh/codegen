package cn.springcloud.codegen.util;

import cn.springcloud.codegen.engine.entity.InputParams;
import cn.springcloud.codegen.engine.tools.ClassTools;
import cn.springcloud.codegen.entity.ProjectModel;
import cn.springcloud.codegen.service.ComponentExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Vincent.
 * @createdOn 2018/03/08 11:27
 */
public class CodeGenTransport {

    private ComponentExecutor componentExecutor;

    public CodeGenTransport(ComponentExecutor componentExecutor){
        this.componentExecutor = componentExecutor;
    }

    public byte[] downloadBytes(ProjectModel projectModel) {
        InputParams inputParams = new InputParams();
        inputParams.setParamMap(ClassTools.buildFieldValueToMap(projectModel));
        return componentExecutor.generate(inputParams);
    }

    public ResponseEntity<Resource> downloadResponse(ProjectModel projectModel) throws URISyntaxException, IOException {
        String canonicalFileName = getCanonicalFileName(projectModel.getProjectName());

        InputParams inputParams = new InputParams();
        inputParams.setParamMap(ClassTools.buildFieldValueToMap(projectModel));
        byte[] bytes = componentExecutor.generate(inputParams);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");

        headers.add("Content-Disposition", "attachment;filename=\"" + canonicalFileName + "\"");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=test.zip")
                .body(new InputStreamResource(new ByteArrayInputStream(bytes)));

    }

    private String getCanonicalFileName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("File name is null or empty");
        }
        try {
            return URLEncoder.encode(fileName + ".zip", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
