package service.chat.mealmate.publish;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/")
public class PublishController{
    @GetMapping("favicon.ico")
    public StreamingResponseBody getFavicon() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/favicon.ico");
        InputStream inputStream = resource.getInputStream();
        return outputStream -> {
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, nRead);
            }
            inputStream.close();
        };
    }
}