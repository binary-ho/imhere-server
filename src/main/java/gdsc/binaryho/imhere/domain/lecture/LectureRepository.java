package gdsc.binaryho.imhere.domain.lecture;

import gdsc.binaryho.imhere.domain.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    Optional<Lecture> findByMember(Member member);
}
