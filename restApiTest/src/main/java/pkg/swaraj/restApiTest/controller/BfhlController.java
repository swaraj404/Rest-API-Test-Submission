package pkg.swaraj.restApiTest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pkg.swaraj.restApiTest.dto.BfhlRequest;
import pkg.swaraj.restApiTest.dto.BfhlResponse;
import pkg.swaraj.restApiTest.service.BfhlService;

@RestController
@RequestMapping("/bfhl")
public class BfhlController {

    private final BfhlService bfhlService;

    public BfhlController(BfhlService bfhlService) {
        this.bfhlService = bfhlService;
    }

    @PostMapping
    public ResponseEntity<BfhlResponse> processData(
            @RequestBody BfhlRequest request,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId) {

        if (requestId == null) {
            requestId = "N/A";
        }
        BfhlResponse response = bfhlService.processData(request, requestId);
        return ResponseEntity.ok(response);
    }
}