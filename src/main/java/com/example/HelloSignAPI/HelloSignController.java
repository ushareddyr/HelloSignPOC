package com.example.HelloSignAPI;

import com.hellosign.sdk.HelloSignException;
import com.hellosign.sdk.resource.Event;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloSignController {
    // Log events we want visibility into
    private final Logger logger = LoggerFactory.getLogger(HelloSignController.class);

    @Autowired
    HelloSignService helloSignService;

    @PostMapping("/hellosign/embeddedsign/{templateId}")
    public String embeddedSignatureRequest(@PathVariable String templateId) throws HelloSignException {
        return helloSignService.sendEmbeddedSignatureRequest(templateId);
    }

    // Bring in environment variables to access values in application.properties
    @Autowired
    private Environment env;

    @PostMapping(value = "/hellosign/webhook")
    public String webhook(@RequestParam String json) throws HelloSignException, JSONException {
        JSONObject jsonObject = new JSONObject(json);
        Event event = new Event(jsonObject);
        // Grab api key from application.properties for verifying HS event
        String hsApiKey = env.getProperty("HS_API_KEY");
        boolean validRequest = event.isValid(hsApiKey);
        String eventPayload = event.toString();
        if (validRequest) {
            switch (event.getTypeString()) {
                case "callback_test":
                    logger.info("Callback Test call:\n" + eventPayload);
                    break;
                case "signature_request_sent":
                    logger.info("Signature Request Sent:\n" + eventPayload);
                    break;
                case "signature_request_all_signed":
// This is the best event to take action for additonal steps
                    logger.info("Signature Request Signed:\n" + eventPayload);
                    break;
                default:
                    logger.info("HS event occured: " + event.getTypeString());
                    break;
            }
        }
        return "Hello API Event Received";
    }
}

