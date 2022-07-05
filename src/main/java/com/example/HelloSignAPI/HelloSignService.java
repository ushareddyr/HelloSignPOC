package com.example.HelloSignAPI;

import com.hellosign.sdk.HelloSignClient;
import com.hellosign.sdk.HelloSignException;
import com.hellosign.sdk.resource.EmbeddedRequest;
import com.hellosign.sdk.resource.EmbeddedResponse;
import com.hellosign.sdk.resource.SignatureRequest;
import com.hellosign.sdk.resource.TemplateSignatureRequest;
import com.hellosign.sdk.resource.support.Signature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


@Service
public class HelloSignService {
    // Bring in environment variables to access values in application.properties
    @Autowired
    private Environment env;

    public String sendEmbeddedSignatureRequest(String templateId) throws HelloSignException {
        // IMPORTANT: fill out application.properties to run sample
        // Grab the user-defined values for calling the HelloSign API
        String hsApiKey = env.getProperty("HS_API_KEY");
        String hsClientId = env.getProperty("HS_CLIENT_ID");
        String hsTemplateId = templateId;
        String signerName = env.getProperty("SIGNER_NAME");
        String signerEmail = env.getProperty("SIGNER_EMAIL");
        String signerRole = env.getProperty("SIGNER_ROLE");

        TemplateSignatureRequest sigRequest = new TemplateSignatureRequest();
        sigRequest.setTemplateId(hsTemplateId);
        sigRequest.setSigner(signerRole, signerEmail, signerName);
        sigRequest.setTestMode(true);

        EmbeddedRequest embedReq = new EmbeddedRequest(hsClientId, sigRequest);
        // Create a HelloSign Client
        HelloSignClient helloSignClient = new HelloSignClient(hsApiKey);
        // Create new Embedded Signature Request
        SignatureRequest newRequest = (SignatureRequest) helloSignClient.createEmbeddedRequest(embedReq);
        // Grab signature id for individual signer
        Signature signature = newRequest.getSignature(signerEmail, signerName);
        String signatureId =  signature.getId();
        // Create sign url to embed in an iFrame
        EmbeddedResponse response = helloSignClient.getEmbeddedSignUrl(signatureId);
        String signUrl = response.getSignUrl();


        return signUrl;
    }
}
