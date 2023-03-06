package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureCreateRequest;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.MemberRepository;
import gdsc.binaryho.imhere.domain.member.Role;
import org.springframework.stereotype.Service;

@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;

    public LectureService(LectureRepository lectureRepository, MemberRepository memberRepository) {
        this.lectureRepository = lectureRepository;
        this.memberRepository = memberRepository;
    }

    public void createLecture(LectureCreateRequest request) {
        Member lecturer = memberRepository.findById(request.getLecturerId()).orElseThrow();
        if (!lecturer.hasRole(Role.LECTURER)) {
            /* TODO: Exception 만들어서 대체 */
            throw new IllegalArgumentException();
        }

        Lecture newLecture = Lecture.createLecture(lecturer, request.getLectureName());
        lectureRepository.save(newLecture);
    }
}
