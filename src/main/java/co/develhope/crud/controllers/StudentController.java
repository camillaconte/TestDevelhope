package co.develhope.crud.controllers;

import co.develhope.crud.entities.Student;
import co.develhope.crud.repositories.StudentRepository;
import co.develhope.crud.services.StudentService;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;

    // create
    @PostMapping("") //se metto lo slash il test poi non funziona...
    //ma poi prova a mettere /create
    public @ResponseBody Student create(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    // read all
    @GetMapping("/")
    //occhio che va inserito lo slash
    public @ResponseBody List<Student> getStudents(){
        return studentRepository.findAll();
    }

    // read just one
    //nel @Test corrispondente la dicitura è "/student/" !!! (occhio allo slash)
    @GetMapping("/{id}")
    public @ResponseBody Student getAStudent(@PathVariable long id){
        Optional<Student> student = studentRepository.findById(id);
        if(student.isPresent()){
            return student.get();
        }else{
            return null;
        }
    }

    // update the id of a student
    @PutMapping("/{id}")
    public @ResponseBody Student updateId(@PathVariable long id, @RequestBody @NotNull Student student){
        student.setId(id);
        return studentRepository.save(student);
    }

    // update the isWorking column of a student
    @PutMapping("/{id}/work")
    public @ResponseBody Student setStudentIsWorking(@PathVariable long id, @RequestParam("working") boolean working){
        return studentService.setStudentIsWorkingStatus(id, working);
    }

    // delete a student
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id){
        studentRepository.deleteById(id);
    }
}
