package com.school.busmanagement.controller;

import com.school.busmanagement.dto.CreateBusRequest;
import com.school.busmanagement.dto.CreateDriverRequest;
import com.school.busmanagement.dto.CreateParentRequest;
import com.school.busmanagement.dto.CreateRouteRequest;
import com.school.busmanagement.dto.CreateStudentRequest;
import com.school.busmanagement.entity.Bus;
import com.school.busmanagement.entity.Driver;
import com.school.busmanagement.entity.Route;
import com.school.busmanagement.entity.Student;
import com.school.busmanagement.service.AuthService;
import com.school.busmanagement.service.BusService;
import com.school.busmanagement.service.DriverService;
import com.school.busmanagement.service.ParentService;
import com.school.busmanagement.service.RouteService;
import com.school.busmanagement.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final StudentService studentService;
    private final AuthService authService;
    private final ParentService parentService;
    private final BusService busService;
    private final RouteService routeService;
    private final DriverService driverService;

    // Render the admin dashboard with summary counts used by the overview cards.
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("studentCount", studentService.countStudents());
        model.addAttribute("parentCount", parentService.countParents());
        model.addAttribute("busCount", busService.countBuses());
        model.addAttribute("routeCount", routeService.countRoutes());
        model.addAttribute("driverCount", driverService.countDrivers());
        return "admin/dashboard";
    }

    // Show the student management table.
    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "admin/students";
    }

    // Render the create student form with available buses and routes.
    @GetMapping("/students/new")
    public String studentForm(Model model) {
        addStudentFormData(model);
        model.addAttribute("createStudentRequest", new CreateStudentRequest());
        model.addAttribute("formAction", "/admin/students/save");
        model.addAttribute("formTitle", "Add Student");
        return "admin/student-form";
    }

    // Save a newly created student and return to the student list.
    @PostMapping("/students/save")
    public String createStudent(@Valid @ModelAttribute CreateStudentRequest createStudentRequest,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            addStudentFormData(model);
            model.addAttribute("formAction", "/admin/students/save");
            model.addAttribute("formTitle", "Add Student");
            return "admin/student-form";
        }
        studentService.createStudent(createStudentRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Student created successfully.");
        return "redirect:/admin/students";
    }

    // Load an existing student into the same form for editing.
    @GetMapping("/students/edit/{id}")
    public String editStudent(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        CreateStudentRequest request = new CreateStudentRequest();
        request.setName(student.getName());
        request.setAge(student.getAge());
        request.setClassName(student.getClassName());
        request.setBusId(student.getBus() != null ? student.getBus().getId() : null);
        request.setRouteId(student.getRoute() != null ? student.getRoute().getId() : null);

        addStudentFormData(model);
        model.addAttribute("createStudentRequest", request);
        model.addAttribute("formAction", "/admin/students/update/" + id);
        model.addAttribute("formTitle", "Edit Student");
        return "admin/student-form";
    }

    // Update an existing student after re-running validation and business rules.
    @PostMapping("/students/update/{id}")
    public String updateStudent(@PathVariable Long id,
                                @Valid @ModelAttribute CreateStudentRequest createStudentRequest,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            addStudentFormData(model);
            model.addAttribute("formAction", "/admin/students/update/" + id);
            model.addAttribute("formTitle", "Edit Student");
            return "admin/student-form";
        }
        studentService.updateStudent(id, createStudentRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully.");
        return "redirect:/admin/students";
    }

    // Delete a student and return to the student list page.
    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        studentService.deleteStudent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully.");
        return "redirect:/admin/students";
    }

    // Show the parent management table.
    @GetMapping("/parents")
    public String parents(Model model) {
        model.addAttribute("parents", parentService.getAllParents());
        return "admin/parents";
    }

    // Render the parent creation form with students that are not already linked.
    @GetMapping("/parents/new")
    public String parentForm(Model model) {
        addParentFormData(model);
        model.addAttribute("createParentRequest", new CreateParentRequest());
        return "admin/parent-form";
    }

    // Create a new parent account linked to a selected student.
    @PostMapping("/parents/save")
    public String createParent(@Valid @ModelAttribute CreateParentRequest createParentRequest,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            addParentFormData(model);
            return "admin/parent-form";
        }
        authService.createParent(createParentRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Parent created successfully.");
        return "redirect:/admin/parents";
    }

    // Show the bus management table.
    @GetMapping("/buses")
    public String buses(Model model) {
        model.addAttribute("buses", busService.getAllBuses());
        model.addAttribute("studentCountsByBus", studentService.getStudentCountsByBus());
        return "admin/buses";
    }

    // Render the create bus form with available drivers and routes.
    @GetMapping("/buses/new")
    public String busForm(Model model) {
        addBusFormData(model);
        model.addAttribute("createBusRequest", new CreateBusRequest());
        model.addAttribute("formAction", "/admin/buses/save");
        model.addAttribute("formTitle", "Add Bus");
        return "admin/bus-form";
    }

    // Save a new bus assignment and return to the bus list.
    @PostMapping("/buses/save")
    public String createBus(@Valid @ModelAttribute CreateBusRequest createBusRequest,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (bindingResult.hasErrors()) {
            addBusFormData(model);
            model.addAttribute("formAction", "/admin/buses/save");
            model.addAttribute("formTitle", "Add Bus");
            return "admin/bus-form";
        }
        busService.createBus(createBusRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Bus created successfully.");
        return "redirect:/admin/buses";
    }

    // Load an existing bus into the same form for editing.
    @GetMapping("/buses/edit/{id}")
    public String editBus(@PathVariable Long id, Model model) {
        Bus bus = busService.getBusById(id);
        CreateBusRequest request = new CreateBusRequest();
        request.setBusNumber(bus.getBusNumber());
        request.setCapacity(bus.getCapacity());
        request.setDriverId(bus.getDriver() != null ? bus.getDriver().getId() : null);
        request.setRouteId(bus.getRoute() != null ? bus.getRoute().getId() : null);

        addBusFormData(model);
        model.addAttribute("createBusRequest", request);
        model.addAttribute("formAction", "/admin/buses/update/" + id);
        model.addAttribute("formTitle", "Edit Bus");
        return "admin/bus-form";
    }

    // Update an existing bus and keep driver/route validation in the service layer.
    @PostMapping("/buses/update/{id}")
    public String updateBus(@PathVariable Long id,
                            @Valid @ModelAttribute CreateBusRequest createBusRequest,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (bindingResult.hasErrors()) {
            addBusFormData(model);
            model.addAttribute("formAction", "/admin/buses/update/" + id);
            model.addAttribute("formTitle", "Edit Bus");
            return "admin/bus-form";
        }
        busService.updateBus(id, createBusRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Bus updated successfully.");
        return "redirect:/admin/buses";
    }

    // Delete a bus after the service confirms no students are assigned to it.
    @GetMapping("/buses/delete/{id}")
    public String deleteBus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        busService.deleteBus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Bus deleted successfully.");
        return "redirect:/admin/buses";
    }

    // Show the route management table.
    @GetMapping("/routes")
    public String routes(Model model) {
        model.addAttribute("routes", routeService.getAllRoutes());
        return "admin/routes";
    }

    // Render the create route form.
    @GetMapping("/routes/new")
    public String routeForm(Model model) {
        model.addAttribute("createRouteRequest", new CreateRouteRequest());
        model.addAttribute("formAction", "/admin/routes/save");
        model.addAttribute("formTitle", "Add Route");
        return "admin/route-form";
    }

    // Save a new route and return to the route list.
    @PostMapping("/routes/save")
    public String createRoute(@Valid @ModelAttribute CreateRouteRequest createRouteRequest,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/admin/routes/save");
            model.addAttribute("formTitle", "Add Route");
            return "admin/route-form";
        }
        routeService.createRoute(createRouteRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Route created successfully.");
        return "redirect:/admin/routes";
    }

    // Load an existing route into the form for editing.
    @GetMapping("/routes/edit/{id}")
    public String editRoute(@PathVariable Long id, Model model) {
        Route route = routeService.getRouteById(id);
        CreateRouteRequest request = new CreateRouteRequest();
        request.setRouteName(route.getRouteName());
        request.setPickupPoints(route.getPickupPoints());

        model.addAttribute("createRouteRequest", request);
        model.addAttribute("formAction", "/admin/routes/update/" + id);
        model.addAttribute("formTitle", "Edit Route");
        return "admin/route-form";
    }

    // Update a route and return to the route list page.
    @PostMapping("/routes/update/{id}")
    public String updateRoute(@PathVariable Long id,
                              @Valid @ModelAttribute CreateRouteRequest createRouteRequest,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/admin/routes/update/" + id);
            model.addAttribute("formTitle", "Edit Route");
            return "admin/route-form";
        }
        routeService.updateRoute(id, createRouteRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Route updated successfully.");
        return "redirect:/admin/routes";
    }

    // Delete a route and return to the route table.
    @GetMapping("/routes/delete/{id}")
    public String deleteRoute(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        routeService.deleteRoute(id);
        redirectAttributes.addFlashAttribute("successMessage", "Route deleted successfully.");
        return "redirect:/admin/routes";
    }

    // Show the driver management table.
    @GetMapping("/drivers")
    public String drivers(Model model) {
        model.addAttribute("drivers", driverService.getAllDrivers());
        return "admin/drivers";
    }

    // Render the create driver form.
    @GetMapping("/drivers/new")
    public String driverForm(Model model) {
        model.addAttribute("createDriverRequest", new CreateDriverRequest());
        model.addAttribute("formAction", "/admin/drivers/save");
        model.addAttribute("formTitle", "Add Driver");
        return "admin/driver-form";
    }

    // Save a new driver and return to the driver list.
    @PostMapping("/drivers/save")
    public String createDriver(@Valid @ModelAttribute CreateDriverRequest createDriverRequest,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/admin/drivers/save");
            model.addAttribute("formTitle", "Add Driver");
            return "admin/driver-form";
        }
        driverService.createDriver(createDriverRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Driver created successfully.");
        return "redirect:/admin/drivers";
    }

    // Load an existing driver into the form for editing.
    @GetMapping("/drivers/edit/{id}")
    public String editDriver(@PathVariable Long id, Model model) {
        Driver driver = driverService.getDriverById(id);
        CreateDriverRequest request = new CreateDriverRequest();
        request.setName(driver.getName());
        request.setPhone(driver.getPhone());

        model.addAttribute("createDriverRequest", request);
        model.addAttribute("formAction", "/admin/drivers/update/" + id);
        model.addAttribute("formTitle", "Edit Driver");
        return "admin/driver-form";
    }

    // Update an existing driver and return to the list page.
    @PostMapping("/drivers/update/{id}")
    public String updateDriver(@PathVariable Long id,
                               @Valid @ModelAttribute CreateDriverRequest createDriverRequest,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/admin/drivers/update/" + id);
            model.addAttribute("formTitle", "Edit Driver");
            return "admin/driver-form";
        }
        driverService.updateDriver(id, createDriverRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Driver updated successfully.");
        return "redirect:/admin/drivers";
    }

    // Delete a driver and return to the driver list page.
    @GetMapping("/drivers/delete/{id}")
    public String deleteDriver(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        driverService.deleteDriver(id);
        redirectAttributes.addFlashAttribute("successMessage", "Driver deleted successfully.");
        return "redirect:/admin/drivers";
    }

    private void addStudentFormData(Model model) {
        model.addAttribute("buses", busService.getAllBuses());
        model.addAttribute("routes", routeService.getAllRoutes());
    }

    private void addParentFormData(Model model) {
        model.addAttribute("students", parentService.getStudentsWithoutParents());
    }

    private void addBusFormData(Model model) {
        model.addAttribute("drivers", driverService.getAllDrivers());
        model.addAttribute("routes", routeService.getAllRoutes());
    }
}
