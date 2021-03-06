package lk.AVSEC.Welfare.asset.employee.controller;

import lk.AVSEC.Welfare.asset.commonAsset.model.Enum.*;
import lk.AVSEC.Welfare.asset.commonAsset.service.CommonService;
import lk.AVSEC.Welfare.asset.dependent.entity.Enum.CurrentStatus;
import lk.AVSEC.Welfare.asset.dependent.entity.Enum.Relationship;
import lk.AVSEC.Welfare.asset.dependent.service.DependentEmployeeService;
import lk.AVSEC.Welfare.asset.dependent.service.DependentService;
import lk.AVSEC.Welfare.asset.designation.service.DesignationService;
import lk.AVSEC.Welfare.asset.employee.entity.Employee;
import lk.AVSEC.Welfare.asset.employee.entity.EmployeeFiles;
import lk.AVSEC.Welfare.asset.employee.entity.Enum.EmployeeStatus;
import lk.AVSEC.Welfare.asset.employee.entity.Enum.Nationality;
import lk.AVSEC.Welfare.asset.employee.entity.Enum.UniformType;
import lk.AVSEC.Welfare.asset.employee.entity.Enum.WelfarePosition;
import lk.AVSEC.Welfare.asset.employee.service.EmployeeFilesService;
import lk.AVSEC.Welfare.asset.employee.service.EmployeeService;
import lk.AVSEC.Welfare.asset.userManagement.entity.User;
import lk.AVSEC.Welfare.asset.userManagement.service.UserService;
import lk.AVSEC.Welfare.asset.workingPlace.service.WorkingPlaceService;
import lk.AVSEC.Welfare.util.service.DateTimeAgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final EmployeeFilesService employeeFilesService;
    private final DateTimeAgeService dateTimeAgeService;
    private final WorkingPlaceService workingPlaceService;
    private final CommonService commonService;
    private final DependentEmployeeService dependentEmployeeService;
    private final UserService userService;
    private final DesignationService designationService;
    private final DependentService dependentService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, EmployeeFilesService employeeFilesService,
                              DateTimeAgeService dateTimeAgeService, WorkingPlaceService workingPlaceService,
                              CommonService commonService,
                              DependentEmployeeService dependentEmployeeService, UserService userService,
                              DesignationService designationService,
                              DependentService dependentService) {
        this.employeeService = employeeService;
        this.employeeFilesService = employeeFilesService;

        this.dateTimeAgeService = dateTimeAgeService;
        this.workingPlaceService = workingPlaceService;
        this.commonService = commonService;
        this.dependentEmployeeService = dependentEmployeeService;
        this.userService = userService;
        this.designationService = designationService;
        this.dependentService = dependentService;
    }
//----> Employee details management - start <----//

    // Common things for an employee add and update
    private String commonThings(Model model) {
        model.addAttribute("title", Title.values());
        model.addAttribute("gender", Gender.values());
        model.addAttribute("civilStatus", CivilStatus.values());
        model.addAttribute("employeeStatus", EmployeeStatus.values());
        model.addAttribute("designation", designationService.findAll());
        model.addAttribute("bloodGroup", BloodGroup.values());
        model.addAttribute("nationality", Nationality.values());
        model.addAttribute("religion", Religion.values());
        model.addAttribute("uniformType", UniformType.values());
        model.addAttribute("relationship", Relationship.values());
        model.addAttribute("currentStatus", CurrentStatus.values());
        model.addAttribute("workingPlaces", workingPlaceService.findAll());
        model.addAttribute("welfarePositions", WelfarePosition.values());
        return "employee/addEmployee";
    }

    //When scr called file will send to
    @GetMapping("/file/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("filename") String filename) {
        EmployeeFiles file = employeeFilesService.findByNewID(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(file.getPic());
    }

    //Send all employee data
    @RequestMapping
    public String employeePage(Model model) {
        List<Employee> employees = new ArrayList<>();
        for (Employee employee : employeeService.findAll()) {
            employee.setFileInfo(employeeFilesService.employeeFileDownloadLinks(employee));
            employees.add(employee);
        }
        /*  Employee employee = employeeService.findById(id);*/
        model.addAttribute("employees", employees);
        model.addAttribute("contendHeader", "Employee List");
//        /* model.addAttribute("files", employeeFilesService.employeeFileDownloadLinks(employee));*/
        return "employee/employee";
    }

    //Send on employee details
    @GetMapping(value = "/{id}")
    public String employeeView(@PathVariable("id") Integer id, Model model) {
        Employee employee = employeeService.findById(id);
        model.addAttribute("employeeDetail", employee);
        model.addAttribute("addStatus", false);
        model.addAttribute("contendHeader", "Employee View Details");
        model.addAttribute("files", employeeFilesService.employeeFileDownloadLinks(employee));
        model.addAttribute("dependentEmployees", dependentEmployeeService.findByEmployee(employee));
        return "employee/employee-detail";
    }

    //Send employee data edit
    @GetMapping(value = "/edit/{id}")
    public String editEmployeeForm(@PathVariable("id") Integer id, Model model) {
        Employee employee = employeeService.findById(id);
        model.addAttribute("employee", employee);
        model.addAttribute("newEmployee", employee.getEpf());
        model.addAttribute("addStatus", false);
        model.addAttribute("contendHeader", "Employee Edit Details");
        model.addAttribute("file", employeeFilesService.employeeFileDownloadLinks(employee));
        return commonThings(model);
    }

    //Send an employee add form
    @GetMapping(value = {"/add"})
    public String employeeAddForm(Model model) {
        model.addAttribute("addStatus", true);
        model.addAttribute("employee", new Employee());
        model.addAttribute("contendHeader", "Employee Add Members");
        return commonThings(model);
    }

//    @GetMapping(value = {"/add"})
//    public String employeeAddForm(Model model) {
//        model.addAttribute("addStatus", true);
//        model.addAttribute("employee", new Employee());
//        model.addAttribute("contendHeader", "Employee Add Members");
//        return commonThings(model);
//    }


    //Employee add and update
    @PostMapping(value = {"/save", "/update"})
    public String addEmployee(@Valid @ModelAttribute Employee employee, BindingResult result, Model model
    ) {
        System.out.println("came employee" + employee.toString());
        if (result.hasErrors()) {
            model.addAttribute("addStatus", true);
            model.addAttribute("employee", employee);
            return commonThings(model);
        }
        try {
            employee.setMobileOne(commonService.commonMobileNumberLengthValidator(employee.getMobileOne()));
            employee.setMobileTwo(commonService.commonMobileNumberLengthValidator(employee.getMobileTwo()));
            employee.setLand(commonService.commonMobileNumberLengthValidator(employee.getLand()));
            // System.out.println("dependent length " + employee.getDependents().size());

            /*int i = employee.getDependents().size();
            if (i != 0) {
                for (int k = 0; k < i; k++) {
                    if (employee.getDependents().get(k).getId() == null) {
                        Dependent dependent = employee.getDependents().get(k);
                        employee.getDependents().add(k, dependent);
                        System.out.println("id not " + k + " name employee id " + employee.getDependents().get(k)
                        .getEmployee().getId());
                    }
                }
            }*/
            //remove current
            //  System.out.println("dependent count " + employee.getDependents().size());
            //after save employee files and save employee
            Employee employeeSaved = employeeService.persist(employee);
            //if employee state is not working he or she cannot access to the system
            if (!employee.getEmployeeStatus().equals(EmployeeStatus.WORKING)) {
                User user = userService.findUserByEmployee(employeeService.findByNic(employee.getNic()));
                //if employee not a user
                if (user != null) {
                    user.setEnabled(false);
                    userService.persist(user);
                }
            }
            //save employee images file
            if (employee.getFile().getOriginalFilename() != null) {
                EmployeeFiles employeeFiles = employeeFilesService.findByName(employee.getFile().getOriginalFilename());
                if (employeeFiles != null) {
                    // update new contents
                    employeeFiles.setPic(employee.getFile().getBytes());
                    // Save all to database
                } else {
                    employeeFiles = new EmployeeFiles(employee.getFile().getOriginalFilename(),
                            employee.getFile().getContentType(),
                            employee.getFile().getBytes(),
                            employee.getNic().concat("-" + LocalDateTime.now()),
                            UUID.randomUUID().toString().concat("employee"));
                    employeeFiles.setEmployee(employee);
                }
                employeeFilesService.persist(employeeFiles);
            }
            employee = employeeSaved;
            return "redirect:/employee";

        } catch (Exception e) {
            ObjectError error = new ObjectError("employee",
                    "There is already in the system. <br>System message -->" + e.toString());
            result.addError(error);
            if (employee.getId() != null) {
                model.addAttribute("addStatus", true);
                System.out.println("id is null");
            } else {
                model.addAttribute("addStatus", false);
            }
            model.addAttribute("employee", employee);
            return commonThings(model);
        }
    }

    //If need to employee {but not applicable for this }
    @GetMapping(value = "/remove/{id}")
    public String removeEmployee(@PathVariable Integer id) {
        employeeService.delete(id);
        return "redirect:/employee";
    }

    //To search employee any giving employee parameter
    @GetMapping(value = "/search")
    public String search(Model model, Employee employee) {
        model.addAttribute("employeeDetail", employeeService.search(employee));
        return "employee/employee-detail";
    }


}
