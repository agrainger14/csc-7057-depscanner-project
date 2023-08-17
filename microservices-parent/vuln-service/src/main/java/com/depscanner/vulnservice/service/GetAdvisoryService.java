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

@Service
@RequiredArgsConstructor
@Transactional
public class GetAdvisoryService {
    private final Mapper mapper;
    private final AdvisoryKeyRepository advisoryKeyRepository;

    public AdvisoryResponse readByAdvisoryKey(AdvisoryKeyDto advisoryKeyRequest) {
        final String advisoryId = advisoryKeyRequest.getId();

        Optional<AdvisoryKey> advisoryKeyOptional = advisoryKeyRepository
                .findByAdvisoryId(advisoryId);

        if (advisoryKeyOptional.isPresent()) {
            return mapper.mapToAdvisoryResponse(advisoryKeyOptional.get());
        }
        return fetchAdvisoryData(advisoryId);
    }

    public AdvisoryResponse fetchAdvisoryData(String advisoryId) {
        String getAdvisoryUrl = ApiHelper.buildApiUrl(ApiHelper.GET_ADVISORY_URL, advisoryId);

        AdvisoryResponse responseDto =
                Optional.ofNullable(ApiHelper.makeApiRequest(getAdvisoryUrl, AdvisoryResponse.class))
                        .orElseThrow(() -> new NoAdvisoryKeyInformationException("No available information for the requested advisory key"));

        createAdvisoryData(responseDto);
        return responseDto;
    }

    public void createAdvisoryData(AdvisoryResponse advisoryResponse) {
        Optional<AdvisoryKey> optionalAdvisory = advisoryKeyRepository.findByAdvisoryId
                (advisoryResponse.getAdvisoryKey().getId());

        AdvisoryKey advisoryKey;

        if (optionalAdvisory.isPresent()) {
            advisoryKey = optionalAdvisory.get();
        } else {
            advisoryKey = AdvisoryKey.builder()
                    .advisoryId(advisoryResponse.getAdvisoryKey().getId())
                    .build();
        }

        AdvisoryDetail advisoryDetail = mapper.mapToAdvisoryDetail(advisoryResponse);
        advisoryKey.setAdvisoryDetail(advisoryDetail);
        advisoryKeyRepository.save(advisoryKey);
    }
}