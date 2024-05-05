Тестируемый код для функционального требования FR1-1 представлен ниже.

@PostMapping("/add")
public String addNote(Model model, @RequestParam MultipartFile file, @RequestParam String dateWith, @RequestParam String dateBy) {
    String result = "";
    try {
        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            String uuidFile = UUID.randomUUID().toString();
            File uploadDir = new File(uploadImg);
            if (!uploadDir.exists()) uploadDir.mkdir();
            result = "note/" + uuidFile + "_" + file.getOriginalFilename();
            file.transferTo(new File(uploadImg + "/" + result));
        }
    } catch (IOException e) {
        model.addAttribute("message", "Некорректные данные!");
        getCurrentUserAndRole(model);
        return "notes";
    }
    noteRepo.save(new Note(result, dateWith, dateBy, getUser()));
    return "redirect:/notes";
}

Тестирующий код для функционального требования FR1-1 представлен ниже.

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
@AutoConfigureMockMvc
public class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAddNote() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Test data".getBytes());
        String dateWith = "2024-04-25";
        String dateBy = "2024-04-26";

        mockMvc.perform(MockMvcRequestBuilders.multipart("/add")
                .file(file)
                .param("dateWith", dateWith)
                .param("dateBy", dateBy))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/notes"));
    }

    @Test
    public void testAddNoteWithInvalidFile() throws Exception {
        // Create a temporary file with invalid content
        Path tempFile = Files.createTempFile("temp", ".txt");
        Files.write(tempFile, "Invalid content".getBytes());
        MockMultipartFile file = new MockMultipartFile(
                "file", "invalid.txt", "text/plain", Files.readAllBytes(tempFile));
        String dateWith = "2024-04-25";
        String dateBy = "2024-04-26";

        mockMvc.perform(MockMvcRequestBuilders.multipart("/add")
                .file(file)
                .param("dateWith", dateWith)
                .param("dateBy", dateBy))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "Некорректные данные!"));
        
        Files.deleteIfExists(tempFile);
    }
}

Тестируемый код для функционального требования FR1-2 представлен ниже.

@GetMapping
public String notes(Model model) {
    getCurrentUserAndRole(model);
    return "notes";
}

Тестирующий код для функционального требования FR1-2 представлен ниже.

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NotesControllerTest {

    @InjectMocks
    private NotesController notesController;

    @Mock
    private Model model;

    @Before
    public void setUp() {
        // Здесь можно добавить дополнительную настройку перед каждым тестом
    }

    @Test
    public void testNotes() {
        // Arrange
        when(notesController.getCurrentUserAndRole(model)).thenReturn(true);

        // Act
        String viewName = notesController.notes(model);

        // Assert
        assertEquals("notes", viewName);
        verify(model, times(1)).addAttribute(anyString(), anyObject());
    }
}

Тестируемый код для функционального требования FR2-1 представлен ниже.

@PostMapping("/fio")
public String fioProfile(@RequestParam String fio) {
    AppUser user = getUser();
    user.setFio(fio);
    userRepo.save(user);
    return "redirect:/profile";
}

@PostMapping("/tel")
public String telProfile(@RequestParam String tel) {
    AppUser user = getUser();
    user.setTel(tel);
    userRepo.save(user);
    return "redirect:/profile";
}

@PostMapping("/email")
public String emailProfile(@RequestParam String email) {
    AppUser user = getUser();
    user.setEmail(email);
    userRepo.save(user);
    return "redirect:/profile";
}

Тестирующий код для функционального требования FR2-1 представлен ниже.
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFioProfile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/fio")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("fio", "Test FIO"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/profile"));
    }

    @Test
    public void testTelProfile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tel")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("tel", "1234567890"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/profile"));
    }

    @Test
    public void testEmailProfile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/email")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "test@example.com"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/profile"));
    }}

Тестируемый код для функционального требования FR2-2 представлен ниже.

@PostMapping("/fio")
public String fioProfile(@RequestParam String fio) {
    AppUser user = getUser();
    user.setFio(fio);
    userRepo.save(user);
    return "redirect:/profile";
}

Тестирующий код для функционального требования FR2-2 представлен ниже.

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepo;

    private AppUser mockUser;

    @Before
    public void setUp() {
        mockUser = new AppUser();
    }

    @Test
    public void testFioProfile() {
        // Arrange
        String newFio = "John Doe";
        when(userController.getUser()).thenReturn(mockUser);

        // Act
        String viewName = userController.fioProfile(newFio);

        // Assert
        assertEquals("redirect:/profile", viewName);
        assertEquals(newFio, mockUser.getFio());
        verify(userRepo, times(1)).save(mockUser);
    }
}

Тестируемый код для функционального требования FR3-1 представлен ниже.

@GetMapping
public String students(Model model) {
    getCurrentUserAndRole(model);
    model.addAttribute("teachers", userRepo.findAllByRole(Role.MANAGER));
    return "teachers";
}

@GetMapping("/search")
public String student(Model model, @RequestParam String fio) {
    getCurrentUserAndRole(model);
    model.addAttribute("fio", fio);
    model.addAttribute("teachers", userRepo.findAllByRoleAndFioContaining(Role.MANAGER, fio));
    return "teachers";
}

Тестирующий код для функционального требования FR3-1 представлен ниже.

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserController userController;

    // Метод теста для проверки работы контроллера students
    @Test
    public void testStudents() throws Exception {
        // Подготовка данных для запроса
        List<User> teachers = Arrays.asList(
                new User("John Doe", Role.MANAGER),
                new User("Jane Smith", Role.MANAGER)
        );
        // Мокирование вызова репозитория для возврата списка учителей
        when(userRepo.findAllByRole(Role.MANAGER)).thenReturn(teachers);

        // Выполнение запроса и проверка результата
        mockMvc.perform(MockMvcRequestBuilders.get("/students"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
                .andExpect(MockMvcResultMatchers.model().attribute("teachers", teachers))
                .andExpect(MockMvcResultMatchers.view().name("teachers"));
    }

    // Метод теста для проверки работы контроллера student
    @Test
    public void testStudent() throws Exception {
        // Параметры запроса
        String fio = "John";
        // Подготовка данных для запроса
        List<User> teachers = Arrays.asList(
                new User("John Doe", Role.MANAGER),
                new User("Johnny Cash", Role.MANAGER)
        );
        // Мокирование вызова репозитория для возврата списка учителей, соответствующих запросу
        when(userRepo.findAllByRoleAndFioContaining(Role.MANAGER, fio)).thenReturn(teachers);

        // Выполнение запроса и проверка результата
        mockMvc.perform(MockMvcRequestBuilders.get("/search").param("fio", fio))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("fio"))
                .andExpect(MockMvcResultMatchers.model().attribute("fio", fio))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
                .andExpect(MockMvcResultMatchers.model().attribute("teachers", teachers))
                .andExpect(MockMvcResultMatchers.view().name("teachers"));
    }
}

Тестируемый код для функционального требования FR3-2 представлен ниже.

@GetMapping("/search")
public String student(Model model, @RequestParam String fio) {
    getCurrentUserAndRole(model);
    model.addAttribute("fio", fio);
    model.addAttribute("teachers", userRepo.findAllByRoleAndFioContaining(Role.MANAGER, fio));
    return "teachers";
}

Тестирующий код для функционального требования FR3-2 представлен ниже.

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @InjectMocks
    private MyController controller;

    @Mock
    private UserRepository userRepo;

    @Mock
    private Model model;

    private List<User> teachers;

    @BeforeEach
    void setUp() {
        teachers = new ArrayList<>();
        teachers.add(new User("John Doe", Role.MANAGER));
        teachers.add(new User("Jane Smith", Role.MANAGER));
    }

    @Test
    void testStudentMethod() {
        String fio = "Doe";
        when(userRepo.findAllByRoleAndFioContaining(Role.MANAGER, fio)).thenReturn(teachers);
        doNothing().when(controller).getCurrentUserAndRole(model);

        String viewName = controller.student(model, fio);

        assertEquals("teachers", viewName);
        verify(model, times(1)).addAttribute("fio", fio);
        verify(model, times(1)).addAttribute("teachers", teachers);
        verify(controller, times(1)).getCurrentUserAndRole(model);
    }
}

Тестируемый код для функционального требования FR4-1 представлен ниже.

@PostMapping("/{subjectId}/absences/add")
public String addAbsence(@PathVariable Long subjectId, @RequestParam Long userId, @RequestParam String date, @RequestParam int count, @RequestParam Reason reason) {
    absenceRepo.save(new Absence(date, count, reason, subjectRepo.getReferenceById(subjectId), userRepo.getReferenceById(userId)));
    return "redirect:/subjects/{subjectId}";
}

Тестирующий код для функционального требования FR4-1 представлен ниже.

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class AbsenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AbsenceRepository absenceRepo;

    @Mock
    private SubjectRepository subjectRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private AbsenceController absenceController;

    @Test
    public void testAddAbsence() throws Exception {
        // Параметры запроса
        Long subjectId = 1L;
        Long userId = 1L;
        String date = "2024-04-25";
        int count = 1;
        Reason reason = Reason.ILLNESS;

        // Выполнение запроса и проверка редиректа
        mockMvc.perform(MockMvcRequestBuilders.post("/" + subjectId + "/absences/add")
                .param("userId", String.valueOf(userId))
                .param("date", date)
                .param("count", String.valueOf(count))
                .param("reason", reason.toString()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/subjects/" + subjectId));

        // Проверка сохранения пропуска в репозитории
        verify(absenceRepo).save(any(Absence.class));
    }
}

Тестируемый код для функционального требования FR4-2 представлен ниже.

@PostMapping("/{subjectId}/absences/{absenceId}/edit")
public String editAbsence(@PathVariable Long subjectId,  @RequestParam String date, @RequestParam int count, @RequestParam Reason reason, @PathVariable Long absenceId) {
    Absence absence = absenceRepo.getReferenceById(absenceId);
    absence.set(date, count, reason);
    absenceRepo.save(absence);
    return "redirect:/subjects/{subjectId}";
}

Тестирующий код для функционального требования FR4-2 представлен ниже.

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @InjectMocks
    private MyController controller;

    @Mock
    private AbsenceRepository absenceRepo;

    private Absence absence;

    @BeforeEach
    void setUp() {
        absence = new Absence();
    }

    @Test
    void testEditAbsenceMethod() {
        Long subjectId = 1L;
        String date = "2023-05-01";
        int count = 3;
        Reason reason = Reason.ILLNESS;
        Long absenceId = 2L;

        when(absenceRepo.getReferenceById(absenceId)).thenReturn(absence);

        String viewName = controller.editAbsence(subjectId, date, count, reason, absenceId);

        verify(absence, times(1)).set(date, count, reason);
        verify(absenceRepo, times(1)).save(absence);
        assertEquals("redirect:/subjects/" + subjectId, viewName);
    }
}

Тестируемый код для функционального требования FR5-1 представлен ниже.

@GetMapping
public String students(Model model) {
    getCurrentUserAndRole(model);
    model.addAttribute("students", userRepo.findAllByRole(Role.USER));
    return "students";
}

@GetMapping("/search")
public String student(Model model, @RequestParam String fio) {
    getCurrentUserAndRole(model);
    model.addAttribute("fio", fio);
    model.addAttribute("students", userRepo.findAllByRoleAndFioContaining(Role.USER, fio));
    return "students";
}
Тестирующий код для функционального требования FR5-1 представлен ниже.

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserController userController;

    // Метод теста для проверки работы контроллера students
    @Test
    public void testStudents() throws Exception {
        // Подготовка данных для запроса
        List<User> students = Arrays.asList(
                new User("John Doe", Role.USER),
                new User("Jane Smith", Role.USER)
        );
        // Мокирование вызова репозитория для возврата списка студентов
        when(userRepo.findAllByRole(Role.USER)).thenReturn(students);

        // Выполнение запроса и проверка результата
        mockMvc.perform(MockMvcRequestBuilders.get("/students"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", students))
                .andExpect(MockMvcResultMatchers.view().name("students"));
    }

    // Метод теста для проверки работы контроллера student
    @Test
    public void testStudent() throws Exception {
        // Параметры запроса
        String fio = "John";
        // Подготовка данных для запроса
        List<User> students = Arrays.asList(
                new User("John Doe", Role.USER),
                new User("Johnny Cash", Role.USER)
        );
        // Мокирование вызова репозитория для возврата списка студентов, соответствующих запросу
        when(userRepo.findAllByRoleAndFioContaining(Role.USER, fio)).thenReturn(students);

        // Выполнение запроса и проверка результата
        mockMvc.perform(MockMvcRequestBuilders.get("/search").param("fio", fio))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("fio"))
                .andExpect(MockMvcResultMatchers.model().attribute("fio", fio))
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", students))
                .andExpect(MockMvcResultMatchers.view().name("students"));
    }
}

Тестируемый код для функционального требования FR5-2 представлен ниже.

@GetMapping("/search")
public String student(Model model, @RequestParam String fio) {
    getCurrentUserAndRole(model);
    model.addAttribute("fio", fio);
    model.addAttribute("students", userRepo.findAllByRoleAndFioContaining(Role.USER, fio));
    return "students";
}

Тестирующий код для функционального требования FR5-2 представлен ниже.

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @InjectMocks
    private MyController controller;

    @Mock
    private UserRepository userRepo;

    @Mock
    private Model model;

    private List<User> students;

    @BeforeEach
    void setUp() {
        students = new ArrayList<>();
        students.add(new User("John Doe", Role.USER));
        students.add(new User("Jane Smith", Role.USER));
    }

    @Test
    void testStudentMethod() {
        String fio = "Doe";
        when(userRepo.findAllByRoleAndFioContaining(Role.USER, fio)).thenReturn(students);
        doNothing().when(controller).getCurrentUserAndRole(model);

        String viewName = controller.student(model, fio);

        assertEquals("students", viewName);
        verify(model, times(1)).addAttribute("fio", fio);
        verify(model, times(1)).addAttribute("students", students);
        verify(controller, times(1)).getCurrentUserAndRole(model);
    }
}

Тестируемый код для функционального требования FR6-1 представлен ниже.

@PostMapping("/{subjectId}/absences/{absenceId}/edit")
public String editAbsence(@PathVariable Long subjectId,  @RequestParam String date, @RequestParam int count, @RequestParam Reason reason, @PathVariable Long absenceId) {
    Absence absence = absenceRepo.getReferenceById(absenceId);
    absence.set(date, count, reason);
    absenceRepo.save(absence);
    return "redirect:/subjects/{subjectId}";
}
Тестирующий код для функционального требования FR6-1 представлен ниже.

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class AbsenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AbsenceRepository absenceRepo;

    @Mock
    private SubjectRepository subjectRepo;

    @InjectMocks
    private AbsenceController absenceController;

    @Test
    public void testEditAbsence() throws Exception {
        // Параметры запроса
        Long subjectId = 1L;
        Long absenceId = 1L;
        String date = "2024-04-25";
        int count = 2;
        Reason reason = Reason.OTHER;

        // Выполнение запроса и проверка редиректа
        mockMvc.perform(MockMvcRequestBuilders.post("/" + subjectId + "/absences/" + absenceId + "/edit")
                .param("date", date)
                .param("count", String.valueOf(count))
                .param("reason", reason.toString()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/subjects/" + subjectId));

        // Проверка обновления пропуска в репозитории
        verify(absenceRepo).save(any(Absence.class));
    }
}
Тестируемый код для функционального требования FR6-2 представлен ниже.

@PostMapping("/{subjectId}/absences/{absenceId}/edit")
public String editAbsence(@PathVariable Long subjectId,  @RequestParam String date, @RequestParam int count, @RequestParam Reason reason, @PathVariable Long absenceId) {
    Absence absence = absenceRepo.getReferenceById(absenceId);
    absence.set(date, count, reason);
    absenceRepo.save(absence);
    return "redirect:/subjects/{subjectId}";
}

Тестирующий код для функционального требования FR6-2 представлен ниже.

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @InjectMocks
    private MyController controller;

    @Mock
    private AbsenceRepository absenceRepo;

    private Absence absence;

    @BeforeEach
    void setUp() {
        absence = new Absence();
    }

    @Test
    void testEditAbsenceMethod() {
        Long subjectId = 1L;
        String date = "2023-05-01";
        int count = 3;
        Reason reason = Reason.ILLNESS;
        Long absenceId = 2L;

        when(absenceRepo.getReferenceById(absenceId)).thenReturn(absence);

        String viewName = controller.editAbsence(subjectId, date, count, reason, absenceId);

        verify(absence, times(1)).set(date, count, reason);
        verify(absenceRepo, times(1)).save(absence);
        assertEquals("redirect:/subjects/" + subjectId, viewName);
    }
}

Тестируемый код для функционального требования FR7-1 представлен ниже.

@GetMapping
public String users(Model model) {
    getCurrentUserAndRole(model);
    model.addAttribute("users", userRepo.findAll());
    model.addAttribute("roles", Role.values());
    return "users";
}

Тестирующий код для функционального требования FR7-1 представлен ниже.

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserController userController;

    @Test
    public void testUsers() throws Exception {
        // Подготовка данных для запроса
        List<User> users = Arrays.asList(
                new User("John Doe", Role.USER),
                new User("Jane Smith", Role.ADMIN)
        );
        // Мокирование вызова репозитория для возврата списка пользователей
        when(userRepo.findAll()).thenReturn(users);

        // Выполнение запроса и проверка результата
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("users"))
                .andExpect(MockMvcResultMatchers.model().attribute("users", users))
                .andExpect(MockMvcResultMatchers.model().attributeExists("roles"))
                .andExpect(MockMvcResultMatchers.model().attribute("roles", Role.values()))
                .andExpect(MockMvcResultMatchers.view().name("users"));
    }
}

Тестируемый код для функционального требования FR7-2 представлен ниже.

@GetMapping("/{id}/enable")
public String enableUser(@PathVariable Long id) {
    AppUser user = userRepo.getReferenceById(id);
    user.setEnabled(true);
    userRepo.save(user);
    return "redirect:/users";
}

Тестирующий код для функционального требования FR7-2 представлен ниже.

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @InjectMocks
    private MyController controller;

    @Mock
    private UserRepository userRepo;

    private AppUser user;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setEnabled(false); // initially disabled
    }

    @Test
    void testEnableUserMethod() {
        Long userId = 1L;

        when(userRepo.getReferenceById(userId)).thenReturn(user);

        String viewName = controller.enableUser(userId);

        verify(user, times(1)).setEnabled(true);
        verify(userRepo, times(1)).save(user);
        assertEquals("redirect:/users", viewName);
    }
}

Тестируемый код для функционального требования FR8-1 представлен ниже.

@PostMapping("/{id}/edit")
public String editUser(@PathVariable Long id, @RequestParam Role role) {
    AppUser user = userRepo.getReferenceById(id);
    user.setRole(role);
    userRepo.save(user);
    return "redirect:/users";
}

Тестирующий код для функционального требования FR8-1 представлен ниже.

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.http.MediaType;

@WebMvcTest(YourController.class) // Замените YourController на имя вашего контроллера
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepo userRepo; // Предположим, что вы имеете репозиторий UserRepo

    @Test
    public void testEditUser() throws Exception {
        // Создаем тестовые данные
        Long userId = 1L;
        Role newRole = Role.ADMIN;

        // Mock вызов userRepo.getReferenceById
        AppUser mockUser = new AppUser();
        when(userRepo.getReferenceById(userId)).thenReturn(mockUser);

        // Выполняем запрос на изменение пользователя
        mockMvc.perform(MockMvcRequestBuilders.post("/" + userId + "/edit")
                .param("role", newRole.toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users"));

        // Проверяем, что метод userRepo.save был вызван с правильными аргументами
        verify(userRepo, times(1)).save(mockUser);
        assertEquals(newRole, mockUser.getRole()); // Проверяем, что роль была установлена правильно
    }
}

Тестируемый код для функционального требования FR8-2 представлен ниже.

@PostMapping("/{id}/edit")
public String editUser(@PathVariable Long id, @RequestParam Role role) {
    AppUser user = userRepo.getReferenceById(id);
    user.setRole(role);
    userRepo.save(user);
    return "redirect:/users";
}

Тестирующий код для функционального требования FR8-2 представлен ниже.

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @InjectMocks
    private MyController controller;

    @Mock
    private UserRepository userRepo;

    private AppUser user;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setRole(Role.USER);
    }

    @Test
    void testEditUserMethod() {
        Long userId = 1L;
        Role newRole = Role.ADMIN;

        when(userRepo.getReferenceById(userId)).thenReturn(user);

        String viewName = controller.editUser(userId, newRole);

        verify(user, times(1)).setRole(newRole);
        verify(userRepo, times(1)).save(user);
        assertEquals("redirect:/users", viewName);
    }
}

Тестируемый код для функционального требования FR9-1 представлен ниже.

@GetMapping("/search")
public String stats(Model model, @RequestParam Long groupId, @RequestParam int year, @RequestParam int month) {
    getCurrentUserAndRole(model);
    model.addAttribute("groupId", groupId);
    model.addAttribute("year", year);
    model.addAttribute("month", month);
    model.addAttribute("groups", groupRepo.findAll());

    List<Absence> absences;

    if (month < 10) {
        absences = absenceRepo.findAllBySubject_Group_IdAndDateContaining(groupId, year + "-0" + month);
    } else {
        absences = absenceRepo.findAllBySubject_Group_IdAndDateContaining(groupId, year + "-" + month);
    }

    absences.sort(Comparator.comparing(Absence::getDate));
    Collections.reverse(absences);

    model.addAttribute("absences", absences);

    int absencesDisrespectful = 0;
    int absencesRespectful = 0;

    for (Absence i : absences) {
        if (i.getReason() == Reason.DISRESPECTFUL) absencesDisrespectful += i.getCount();
        if (i.getReason() == Reason.RESPECTFUL) absencesRespectful += i.getCount();
    }

    model.addAttribute("absencesDisrespectful", absencesDisrespectful);
    model.addAttribute("absencesRespectful", absencesRespectful);

    return "stats";
}

Тестирующий код для функционального требования FR9-1 представлен ниже.

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Метод теста для проверки работы контроллера stats
    @Test
    public void testStats() throws Exception {
        // Параметры запроса
        Long groupId = 1L;
        int year = 2024;
        int month = 4;

        // Подготовка данных для запроса
        List<Absence> absences = new ArrayList<>();
        // Добавление тестовых данных в список absences
        // Здесь можно добавить любые тестовые данные, необходимые для проверки
        // Например:
        // absences.add(new Absence(...));
        
        // Выполнение запроса и проверка результата
        mockMvc.perform(MockMvcRequestBuilders.get("/search")
                .param("groupId", groupId.toString())
                .param("year", String.valueOf(year))
                .param("month", String.valueOf(month)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // Проверка модели и атрибутов
                .andExpect(MockMvcResultMatchers.model().attributeExists("groupId"))
                .andExpect(MockMvcResultMatchers.model().attribute("groupId", groupId))
                .andExpect(MockMvcResultMatchers.model().attributeExists("year"))
                .andExpect(MockMvcResultMatchers.model().attribute("year", year))
                .andExpect(MockMvcResultMatchers.model().attributeExists("month"))
                .andExpect(MockMvcResultMatchers.model().attribute("month", month))
                .andExpect(MockMvcResultMatchers.model().attributeExists("groups"))
                // Проверка списка absences
                .andExpect(MockMvcResultMatchers.model().attributeExists("absences"))
                // Добавьте здесь другие проверки, если необходимо
                .andExpect(MockMvcResultMatchers.model().attributeExists("absencesDisrespectful"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("absencesRespectful"))
                // Проверка возвращаемого представления
                .andExpect(MockMvcResultMatchers.view().name("stats"));
    }
}

Тестируемый код для функционального требования FR9-2 представлен ниже.

@GetMapping
public String stats(Model model) {
    getCurrentUserAndRole(model);
    model.addAttribute("groups", groupRepo.findAll());
    List<Absence> absences = absenceRepo.findAll();

    absences.sort(Comparator.comparing(Absence::getDate));
    Collections.reverse(absences);

    model.addAttribute("absences", absences);

    int absencesDisrespectful = 0;
    int absencesRespectful = 0;

    for (Absence i : absences) {
        if (i.getReason() == Reason.DISRESPECTFUL) absencesDisrespectful += i.getCount();
        if (i.getReason() == Reason.RESPECTFUL) absencesRespectful += i.getCount();
    }

    model.addAttribute("absencesDisrespectful", absencesDisrespectful);
    model.addAttribute("absencesRespectful", absencesRespectful);

    return "stats";
}

Тестирующий код для функционального требования FR9-2 представлен ниже.

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @InjectMocks
    private MyController controller;

    @Mock
    private GroupRepository groupRepo;

    @Mock
    private AbsenceRepository absenceRepo;

    @Mock
    private Model model;

    private List<Absence> absences;

    @BeforeEach
    void setUp() {
        absences = new ArrayList<>();
        absences.add(new Absence("2023-05-01", 3, Reason.DISRESPECTFUL));
        absences.add(new Absence("2023-05-02", 2, Reason.RESPECTFUL));
        absences.add(new Absence("2023-05-03", 1, Reason.DISRESPECTFUL));
    }

    @Test
    void testStatsMethod() {
        when(groupRepo.findAll()).thenReturn(new ArrayList<>());
        when(absenceRepo.findAll()).thenReturn(absences);

        String viewName = controller.stats(model);

        verify(model, times(1)).addAttribute("groups", new ArrayList<>());
        verify(model, times(1)).addAttribute("absences", absences);
        verify(model, times(1)).addAttribute("absencesDisrespectful", 4);
        verify(model, times(1)).addAttribute("absencesRespectful", 2);
        assertEquals("stats", viewName);
    }
}

Тестируемый код для функционального требования FR10-1 представлен ниже.

@PostMapping("/fio")
public String fioProfile(@RequestParam String fio) {
    AppUser user = getUser();
    user.setFio(fio);
    userRepo.save(user);
    return "redirect:/profile";
}

Тестирующий код для функционального требования FR10-1 представлен ниже.

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFioProfile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/fio")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("fio", "Test FIO"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/profile"));
    }
   
Тестируемый код для функционального требования FR10-2 представлен ниже.

@PostMapping("/fio")
public String fioProfile(@RequestParam String fio) {
    AppUser user = getUser();
    user.setFio(fio);
    userRepo.save(user);
    return "redirect:/profile";
}

Тестирующий код для функционального требования FR10-2 представлен ниже.

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @InjectMocks
    private MyController controller;

    @Mock
    private UserRepository userRepo;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Test
    void testFioProfileMethod() {
        String newFio = "John Doe";
        AppUser user = new AppUser();

        when(controller.getUser()).thenReturn(user);

        String viewName = controller.fioProfile(newFio);

        verify(user, times(1)).setFio(newFio);
        verify(userRepo, times(1)).save(user);
        assertEquals("redirect:/profile", viewName);
    }
    @Test
    void testFioProfileMethod() {
        String newFio = "John Doe";
        AppUser user = new AppUser();

        when(controller.getUser()).thenReturn(user);

        String viewName = controller.fioProfile(newFio);

        verify(user, times(1)).setFio(newFio);
        verify(userRepo, times(1)).save(user);
        assertEquals("redirect:/profile", viewName);
    }

    @Test
    void testGetUserMethod() {
        AppUser expectedUser = new AppUser();
        expectedUser.setUsername("testuser");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(expectedUser);

        AppUser actualUser = controller.getUser();

        assertEquals(expectedUser, actualUser);
    }
}

Далее с помощью созданных юнит-тестов будет определена оценка тестового покрытия кода.


