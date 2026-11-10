package kh.edu.istad.platform.iam.persistence.repository;

import kh.edu.istad.platform.iam.domain.model.IdentityProvider;
import kh.edu.istad.platform.iam.persistence.entity.IdentityJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IdentitySpringDataRepository extends JpaRepository<IdentityJpaEntity, Long> {
    Optional<IdentityJpaEntity> findByUuid(UUID uuid);
    List<IdentityJpaEntity> findByUserId(Long userId);
    Optional<IdentityJpaEntity> findByProviderAndProviderSubject(IdentityProvider provider, String providerSubject);
}
