package team.seventhmile.tripforp.domain.region.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.region.dto.CreateRegionRequest;
import team.seventhmile.tripforp.domain.region.dto.GetPopularRegionsResponse;
import team.seventhmile.tripforp.domain.region.entity.Province;
import team.seventhmile.tripforp.domain.region.entity.Region;
import team.seventhmile.tripforp.domain.region.repository.RegionRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RegionService {

    private final RegionRepository regionRepository;

    @Transactional
    @CacheEvict(value = "regions", key = "#request.province")
    public Long createRegion(CreateRegionRequest request) {
        Region region = Region.builder()
            .province(Province.findByName(request.getProvince()))
            .city(request.getCity())
            .build();
        regionRepository.save(region);

        return region.getId();
    }

    @Transactional
    @CacheEvict(value = "regions", key = "#result.province.name")
    public Region deleteRegion(Long id) {
        Region region = regionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Region.class, id));

        regionRepository.delete(region);
        return region;
    }


    public List<String> getProvinceList() {
        List<String> result = new ArrayList<>();
        for (Province province : Province.values()) {
            result.add(province.getName());
        }
        return result;
    }

    @Cacheable(value = "regions", key = "#province")
    public List<String> getCityListByProvince(String province) {
        List<Region> regions = regionRepository.findByProvince(Province.findByName(province));
        List<String> result = new ArrayList<>();
        for (Region region : regions) {
            result.add(region.getCity());
        }
        return result;
    }

    @Cacheable(value = "popularRegions", key = "'popular'")
    public List<GetPopularRegionsResponse> getPopularRegions(int size) {
        return regionRepository.getPopularRegions(size);
    }

}
