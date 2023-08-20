package gdsc.binaryho.imhere.core.lecture.infrastructure;

import gdsc.binaryho.imhere.config.redis.RedisKeyPrefixes;
import gdsc.binaryho.imhere.core.lecture.Lecture;
import gdsc.binaryho.imhere.core.lecture.application.port.LectureStudentRepository;
import gdsc.binaryho.imhere.core.member.Member;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LectureStudentRedisRepository implements LectureStudentRepository {

    private static final int LECTURE_STUDENT_EXPIRE_TIME = 10;
    private static final String KEY_PREFIX = RedisKeyPrefixes.LECTURE_STUDENT_KEY_PREFIX;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void saveLectureStudents(Lecture lecture, List<Member> students) {
        final String lectureId = lecture.getId().toString();

        students
            .forEach(student -> saveStudent(student, lectureId));
    }

    @Override
    public Set<Long> findLectureIdByStudentId(Long studentId) {
        String queryKey = KEY_PREFIX + studentId.toString();
        DataType dataType = redisTemplate.type(queryKey);

        StudentReadStrategy readStrategy = StudentReadStrategy.fromDataType(dataType);
        return readStrategy.findLectureIds(redisTemplate, queryKey);
    }

    private void saveStudent(Member student, String lectureId) {
        String key = KEY_PREFIX + student.getId().toString();
        DataType dataType = redisTemplate.type(key);
        StudentSaveStrategy saveStrategy = StudentSaveStrategy.fromDataType(dataType);
        saveStrategy.saveStudent(redisTemplate, key, lectureId);
        redisTemplate.expire(key, LECTURE_STUDENT_EXPIRE_TIME, TimeUnit.MINUTES);
    }
}
