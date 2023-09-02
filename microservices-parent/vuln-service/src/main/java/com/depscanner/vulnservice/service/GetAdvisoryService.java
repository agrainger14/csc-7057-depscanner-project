package com.depscanner.vulnservice.service;

import com.depscanner.vulnservice.depsdev.ApiHelper;
import com.depscanner.vulnservice.exception.NoAdvisoryKeyInformationException;
import com.depscanner.vulnservice.mapper.Mapper;
import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryKeyDto;
import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryResponse;
import com.depscanner.vulnservice.model.entity.AdvisoryDetail;
import com.depscanner.vulnservice.model.entity.AdvisoryKey;
import com.depscanner.vulnservice.repository.AdvisoryKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class responsible for retrieving and managing advisory information.
 * This service provides methods to read advisory data by advisory key, fetch advisory data from the deps.dev API
 * and create or update advisory information in the database.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class GetAdvisoryService {
    private final Mapper mapper;
    private final AdvisoryKeyRepository advisoryKeyRepository;

    /**
     * Reads advisory data by the provided advisory key.
     *
     * @param advisoryKeyRequest The advisory key DTO containing the identifier for the advisory.
     * @return An {@link AdvisoryResponse} containing the advisory information.
     * @throws NoAdvisoryKeyInformationException if no information is available for the requested advisory key.
     */
    public AdvisoryResponse readByAdvisoryKey(AdvisoryKeyDto advisoryKeyRequest) {
        final String advisoryId = advisoryKeyRequest.getId();

        Optional<AdvisoryKey> advisoryKeyOptional = advisoryKeyRepository
                .findByAdvisoryId(advisoryId);

        if (advisoryKeyOptional.isPresent()) {
            return mapper.mapToAdvisoryResponse(advisoryKeyOptional.get());
        }
        return fetchAdvisoryData(advisoryId);
    }

    /**
     * Fetches advisory data from the deps.dev API by advisory key.
     *
     * @param advisoryId The advisory key to fetch data for.
     * @return An {@link AdvisoryResponse} containing the fetched advisory information.
     * @throws NoAdvisoryKeyInformationException if no information is available for the requested advisory key.
     */
    public AdvisoryResponse fetchAdvisoryData(String advisoryId) {
        String getAdvisoryUrl = ApiHelper.buildApiUrl(ApiHelper.GET_ADVISORY_URL, advisoryId);

        AdvisoryResponse responseDto =
                Optional.ofNullable(ApiHelper.makeApiRequest(getAdvisoryUrl, AdvisoryResponse.class))
                        .orElseThrow(() -> new NoAdvisoryKeyInformationException("No available information for the requested advisory key"));

        createAdvisoryData(responseDto);
        return responseDto;
    }

    /**
     * Creates or updates advisory data in the database.
     *
     * @param advisoryResponse The advisory response containing advisory information to be saved.
     */
    public void createAdvisoryData(AdvisoryResponse advisoryResponse) {
        Optional<AdvisoryKey> optionalAdvisory = advisoryKeyRepository.findByAdvisoryId
                (advisoryResponse.getAdvisoryKey().getId());

        AdvisoryKey advisoryKey;

        advisoryKey = optionalAdvisory.orElseGet(() -> AdvisoryKey.builder()
                .advisoryId(advisoryResponse.getAdvisoryKey().getId())
                .build());

        AdvisoryDetail advisoryDetail = mapper.mapToAdvisoryDetail(advisoryResponse);
        advisoryKey.setAdvisoryDetail(advisoryDetail);
        advisoryKeyRepository.save(advisoryKey);
    }
}