package bjs.zangbu.security.config;

import javax.servlet.ServletContext;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.multipart.support.MultipartFilter;

public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {
//    private CharacterEncodingFilter encodingFilter() {
//        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
//        encodingFilter.setEncoding("UTF-8");
//        encodingFilter.setForceEncoding(true);
//        return encodingFilter;
//    }

  @Override
  protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
    insertFilters(servletContext, new MultipartFilter());
  }

}
