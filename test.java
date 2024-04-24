
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


@PostMapping("/{subjectId}/absences/add")
public String addAbsence(@PathVariable Long subjectId, @RequestParam Long userId, @RequestParam String date, @RequestParam int count, @RequestParam Reason reason) {
    absenceRepo.save(new Absence(date, count, reason, subjectRepo.getReferenceById(subjectId), userRepo.getReferenceById(userId)));
    return "redirect:/subjects/{subjectId}";
}



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



@PostMapping("/{subjectId}/absences/{absenceId}/edit")
public String editAbsence(@PathVariable Long subjectId,  @RequestParam String date, @RequestParam int count, @RequestParam Reason reason, @PathVariable Long absenceId) {
    Absence absence = absenceRepo.getReferenceById(absenceId);
    absence.set(date, count, reason);
    absenceRepo.save(absence);
    return "redirect:/subjects/{subjectId}";
}

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


@GetMapping
public String users(Model model) {
    getCurrentUserAndRole(model);
    model.addAttribute("users", userRepo.findAll());
    model.addAttribute("roles", Role.values());
    return "users";
}


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



@PostMapping("/{id}/edit")
public String editUser(@PathVariable Long id, @RequestParam Role role) {
    AppUser user = userRepo.getReferenceById(id);
    user.setRole(role);
    userRepo.save(user);
    return "redirect:/users";
}



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



@PostMapping("/fio")
public String fioProfile(@RequestParam String fio) {
    AppUser user = getUser();
    user.setFio(fio);
    userRepo.save(user);
    return "redirect:/profile";
}


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
   
Далее с помощью созданных юнит-тестов будет определена оценка тестового покрытия кода.
