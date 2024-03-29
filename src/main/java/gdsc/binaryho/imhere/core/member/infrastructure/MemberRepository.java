package gdsc.binaryho.imhere.core.member.infrastructure;

import gdsc.binaryho.imhere.core.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findById(Long id);
    Optional<Member> findByUnivId(String univId);
}
