package lk.AVSEC.Welfare.asset.workingPlace.controller;

import lk.AVSEC.Welfare.asset.commonAsset.model.Enum.Province;
import lk.AVSEC.Welfare.asset.district.controller.DistrictController;
import lk.AVSEC.Welfare.asset.district.service.DistrictService;
import lk.AVSEC.Welfare.asset.workingPlace.entity.Enum.AirportType;
import lk.AVSEC.Welfare.asset.workingPlace.entity.Enum.ShortName;
import lk.AVSEC.Welfare.asset.workingPlace.entity.Enum.WorkingPlaceSection;
import lk.AVSEC.Welfare.asset.workingPlace.entity.WorkingPlace;
import lk.AVSEC.Welfare.asset.workingPlace.service.WorkingPlaceService;
import lk.AVSEC.Welfare.util.interfaces.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;


@Controller
@RequestMapping("/workingPlace")
public class WorkingPlaceController implements AbstractController<WorkingPlace, Integer> {
    private final WorkingPlaceService workingPlaceService;
    private final DistrictService districtService;

    @Autowired
    public WorkingPlaceController(WorkingPlaceService workingPlaceService, DistrictService districtService) {
        this.workingPlaceService = workingPlaceService;
        this.districtService = districtService;
    }

    private String commonThing(Model model, Boolean booleanValue, WorkingPlace workingPlaceObject) {
        model.addAttribute("addStatus", booleanValue);
        /*Start array*/
        model.addAttribute("provinces", Province.values());
        model.addAttribute("shortNames", ShortName.values());
        model.addAttribute("airportTypes", AirportType.values());
        model.addAttribute("workingPlaceSections", WorkingPlaceSection.values());
        /*End array*/
        /*district find url*/
        model.addAttribute("districtFindUrl", MvcUriComponentsBuilder
                .fromMethodName(DistrictController.class, "getDistrictByProvince","")
                .build()
                .toString());
        model.addAttribute("workingPlace", workingPlaceObject);
        return "workingPlace/addWorkingPlace";
    }

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("workingPlaces", workingPlaceService.findAll());
        return "workingPlace/workingPlace";
    }

    @GetMapping("/add")
    public String form(Model model) {
        return commonThing(model, false, new WorkingPlace());
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Integer id, Model model) {
        model.addAttribute("workingPlaceDetail", workingPlaceService.findById(id));
        return "workingPlace/workingPlace-detail";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("districts",districtService.findAll());
        return commonThing(model, true, workingPlaceService.findById(id));
    }

    @PostMapping(value = {"/save", "/update"})
    public String persist(@Valid @ModelAttribute WorkingPlace workingPlace, BindingResult bindingResult,
                          RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            return commonThing(model, false, workingPlace);

        }
        redirectAttributes.addFlashAttribute("workingPlaceDetail", workingPlaceService.persist(workingPlace));
        return "redirect:/workingPlace";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, Model model) {
        workingPlaceService.delete(id);
        return "redirect:/workingPlace";
    }


}