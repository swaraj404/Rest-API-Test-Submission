package pkg.swaraj.restApiTest.service;

import pkg.swaraj.restApiTest.dto.BfhlRequest;
import pkg.swaraj.restApiTest.dto.BfhlResponse;

public interface BfhlService {
    BfhlResponse processData(BfhlRequest request, String requestId);
}