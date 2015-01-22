package com.netaporter.test.utils.http;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.RedirectConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import static com.jayway.restassured.config.SSLConfig.sslConfig;

/**
 * Created with IntelliJ IDEA.
 * User: a.kogan@london.net-a-porter.com
 * Date: 25/03/2013
 * Time: 14:55
 */
@Component
public class RestAssuredTrustStoreConfigurator {

    @Value("${truststore.path}")
    private String trustStorePath;

    @Value("${truststore.password}")
    private String trustStorePassword;

    @PostConstruct
    public void configureTrustStore()  {
        if ((trustStorePath != null) && (trustStorePassword != null)) {
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(this.getClass().getResourceAsStream(trustStorePath), trustStorePassword.toCharArray());
                RestAssured.config = RestAssured.config().sslConfig(sslConfig().with().trustStore(trustStore));
                RestAssured.keystore(trustStorePath,trustStorePassword);

            } catch (KeyStoreException e) {
                throw new RuntimeException(e);
            } catch (CertificateException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
        RestAssured.config = RestAssured.config().redirect(RedirectConfig.redirectConfig().allowCircularRedirects(true).rejectRelativeRedirect(false).followRedirects(true));
    }

}