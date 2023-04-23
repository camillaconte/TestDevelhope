package co.develhope.crud;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import co.develhope.crud.controllers.StudentController;
import co.develhope.crud.entities.Student;
import static org.assertj.core.api.Assertions.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@SpringBootTest
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
class StudentControllerTest {

    @Autowired
    private StudentController studentController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void studentControllerLoads() {
        assertThat(studentController).isNotNull();
    }

    //Metodo "AUSILIARIO"
    //per il metodo di test @Test readSingleStudent
    private Student getStudentFromId(Long id) throws Exception{
        //fai una API request di tipo GET
        //per risalire a uno studente tramite il suo id
        MvcResult result = this.mockMvc.perform(get("/student/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //una volta che ho il mio MvcResult
        // (che corrisponde alla domanda "Com'è andata questa GET?")
        //voglio anche ritornare lo Studente salvato
        //ma già che ci sono voglio anche sapere
        //che sia lo studente che il suo id NON SONO NULLI
        try {
            String studentJSON = result.getResponse().getContentAsString();
            Student student = objectMapper.readValue(studentJSON, Student.class);

            assertThat(student).isNotNull();
            assertThat(student.getId()).isNotNull();

            return student;
        }catch (Exception e){
            return null;
        }
    }

    //lorenzo non lo annota con @Test...
    //vedi la sequela di metodi sotto, per capire meglio
    //all'inizio crea un Test con un sacco di codice
    //poi decide invece di spezzettare quel codice
    //in alcune FUNZIONI AUSILIARIE PRIVATE
    //che poi "confluiscono" in un @Test
    //perché possono servire in più di un test!!!
    //e solo alla fine crea un metodo Test

    /*
    QUESTO è IL GIRO CHE FANNO I METODI:
    createAStudent()<-- createAStudent(Student) <-- createStudentRequest(student)

    il RISULTATO FINALE è un metodo (createAStudent) che
    - restituisce uno studente
    - stampa un McVResult
    .......GIUSTOOOOO?
     */

    //TRATTANDOSI DI UN TEST
    //devo per forza dargli io un esempio di specifico studente!
    private Student createAStudent() throws Exception {
        Student student = new Student();
        student.setName("Paul");
        student.setSurname("Burns");
        student.setWorking(true);
        //sfrutto il metodo che vedi qui sotto
        //che restituisce uno studente dopo averlo creato nel Mock DB
        return createAStudent(student);
    }

    //faccio l'Overload del metodo qui sopra
    private Student createAStudent(Student student) throws Exception {
        //uso un altro metodo "ausiliario" creato sotto
        //più che altro perché altrimenti veniva chilometrico
        //e forse anche per potere riutilizzare il codice!
        //questo metodo ausiliario
        //restituisce un McvResult
        MvcResult result = createAStudentRequest(student);
        //però NON MI BASTA avere l'Mvc result
        //voglio anche che venga restituito l'oggetto di tipo Student
        //che avevo dato in ingresso
        Student studentFromResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Student.class);

        assertThat(studentFromResponse).isNotNull();
        assertThat(studentFromResponse.getId()).isNotNull();

        return studentFromResponse;
    }

    //questo metodo non restituisce uno Student
    //ma un MvcResult!
    //però N.B. in realtà POI NON LO USO per arrivare al metodo
    //@Test createAStudentTest
    private MvcResult createAStudentRequest() throws Exception {
        Student student = new Student();
        student.setName("Paul");
        student.setSurname("Burns");
        student.setWorking(true);
        return createAStudentRequest(student);
    }

    //https://www.baeldung.com/jackson-object-mapper-tutorial
    //anche qui OVERLOAD del metodo precedente
    private MvcResult createAStudentRequest(Student student) throws Exception {
        if(student == null) return null;
        //SE INVECE LO STUDENTE non è nullo, quinsi è STATO FORNITO
        //lo trasformo in JSON
        String studentJSON = objectMapper.writeValueAsString(student);
        //uso quel JSON per RITORNARE UN MvcResult
        //FAI ("perform") UNA RICHIESTA API di tipo POST
        //e restituiscimi il risultato
        //"result" in un certo senso è == a una RESPONSE, giusto???
        //infatti la parola "result" mi sembra che faccia confusione
        //se usassimo RESPONSE forse sarebbe più chiaro?
        return this.mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJSON))
                .andDo(print()) //stampa tutta la response
                .andExpect(status().isOk())
                .andReturn(); //termina la tua chiamata
    }

    //------ ED ECCO IL METODO DI TEST CHE RISULTA DAI (tre su quattro) METODI SOPRA!!! ------
    @Test
    void createAStudentTest() throws Exception {
        Student studentFromResponse = createAStudent();
    }


    @Test
    void readStudentsList() throws Exception {
        //fai una richiesta API per creare e inserire uno studente!!!
        //questo rende il test indipendente
        //rispetto a un altro test dove viene creato un utente
        //ogni test deve essere a sè, giusto???
        createAStudentRequest();
        //fai una richiesta API di tipo GET
        //e stampa il risultato
        MvcResult result =this.mockMvc.perform(get("/student/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<Student> studentsFromResponse = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
        System.out.println("Students in database are: " + studentsFromResponse.size());
        assertThat(studentsFromResponse.size()).isNotZero();
    }

    @Test
    void readSingleStudent() throws Exception {
        //creo uno studente nel Mock Database
        Student student = createAStudent();
        //provo a recuperare quello studente
        Student studentFromResponse = getStudentFromId(student.getId());
        //dimmi che l'id dello studente che hai recuperato dal DB
        //è uguale all'id dello studente che avevi creato
        //questo per testare il fatto che la GET funzioni bene
        //(e che non vada a prendere un altro id ???)
        assertThat(studentFromResponse.getId()).isEqualTo(student.getId());
    }

    @Test
    void updateStudent() throws Exception{
        Student student = createAStudent();

        String newName = "Frank";
        student.setName(newName);

        String studentJSON = objectMapper.writeValueAsString(student);

        //Voglio il RISULTATO (la risposta) di una API Request di tipo PUT
        MvcResult result = this.mockMvc.perform(put("/student/"+student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //ora mi prendo lo studente dentro il risultato della PUT
        Student studentFromResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Student.class);

        assertThat(studentFromResponse.getId()).isEqualTo(student.getId());
        assertThat(studentFromResponse.getName()).isEqualTo(newName);

        //Per verificare che lo studente sia stato davvero aggiornato
        //devo fare anche una GET !!!
        Student studentFromResponseGet = getStudentFromId(student.getId());
        assertThat(studentFromResponseGet.getId()).isEqualTo(student.getId());
        assertThat(studentFromResponseGet.getName()).isEqualTo(newName);
    }

    @Test
    void deleteStudent() throws Exception{
        Student student = createAStudent();
        assertThat(student.getId()).isNotNull();

        this.mockMvc.perform(delete("/student/"+student.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //per verificare che lo studente sia stato davvero cancellato
        //devo fare anche una richiesta GET
        Student studentFromResponseGet = getStudentFromId(student.getId());
        assertThat(studentFromResponseGet).isNull();
    }

    @Test
    void activateStudent() throws Exception{
        Student student = createAStudent();
        assertThat(student.getId()).isNotNull();

        MvcResult result = this.mockMvc.perform(put("/student/"+student.getId()+"/work?working=true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Student studentFromResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Student.class);
        assertThat(studentFromResponse).isNotNull();
        assertThat(studentFromResponse.getId()).isEqualTo(student.getId());
        assertThat(studentFromResponse.isWorking()).isEqualTo(true);

        Student studentFromResponseGet = getStudentFromId(student.getId());
        assertThat(studentFromResponseGet).isNotNull();
        assertThat(studentFromResponseGet.getId()).isEqualTo(student.getId());
        assertThat(studentFromResponseGet.isWorking()).isEqualTo(true);
    }

}
