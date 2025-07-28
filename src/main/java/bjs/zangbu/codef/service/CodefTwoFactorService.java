package bjs.zangbu.codef.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.UnsupportedEncodingException;
public interface CodefTwoFactorService {

    String residentRegistrationCertificate(Object request) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;

    String generalBuildingLeader (Object request) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;
}
