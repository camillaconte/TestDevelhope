package co.develhope.crud;

import co.develhope.crud.entities.Student;
import co.develhope.crud.repositories.StudentRepository;
import co.develhope.crud.services.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles(value = "test")
public class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void checkStudentActivation() throws Exception {
        Student student = new Student();
        student.setName("Paul");
        student.setSurname("Burns");
        student.setWorking(true);

        //qui sto testando il fatto che venga salvato
        //quindi sto testando la Repository, non il service!!!
        Student studentFromDB = studentRepository.save(student);
        assertThat(studentFromDB).isNotNull();
        assertThat(studentFromDB.getId()).isNotNull();

        //qui testo il fatto che il metodo del Service setStudentisWorkingStatus
        //funzioni bene
        Student studentFromService = studentService.setStudentIsWorkingStatus(student.getId(), true);
        assertThat(studentFromService).isNotNull();
        assertThat(studentFromService.getId()).isNotNull();
        assertThat(studentFromService.isWorking()).isTrue();

        //qui testo il metodo della Repository
        //per cercare uno studente
        //NON STIAMO FACENDO TROPPE COSE INSIEME?????
        Student studentFromFind = studentRepository.findById(studentFromDB.getId()).get();
        assertThat(studentFromFind).isNotNull();
        assertThat(studentFromFind.getId()).isNotNull();
        assertThat(studentFromFind.getId()).isEqualTo(studentFromDB.getId());
        assertThat(studentFromFind.isWorking()).isTrue();
    }
}
