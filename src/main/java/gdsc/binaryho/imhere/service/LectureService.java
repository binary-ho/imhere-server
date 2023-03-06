package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureCreateRequest;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import gdsc.binaryho.imhere.domain.lecturestudent.LectureStudent;
import gdsc.binaryho.imhere.domain.lecturestudent.LectureStudentRepository;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.MemberRepository;
import gdsc.binaryho.imhere.domain.member.Role;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private final LectureStudentRepository lectureStudentRepository;
    private final MemberRepository memberRepository;

    public LectureService(LectureRepository lectureRepository,
        LectureStudentRepository lectureStudentRepository,
        MemberRepository memberRepository) {
        this.lectureRepository = lectureRepository;
        this.lectureStudentRepository = lectureStudentRepository;
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

    public List<Lecture> getStudentLectures(Long studentId) {
        List<LectureStudent> lectureStudents = lectureStudentRepository.findAllByMemberId(studentId);
        return getLectures(lectureStudents);
    }

    public List<Lecture> getStudentOpenLectures(Long studentId) {
        List<LectureStudent> lectureStudents = lectureStudentRepository.findAllByMemberIdAndLecture_LectureState(studentId, LectureState.OPEN);
        return getLectures(lectureStudents);
    }

    private List<Lecture> getLectures(List<LectureStudent> lectureStudents) {
        return lectureStudents.stream()
            .map(LectureStudent::getLecture)
            .collect(Collectors.toList());
    }
}
