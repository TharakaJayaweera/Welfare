package lk.AVSEC.Welfare.asset.briefing.controller;

import lk.AVSEC.Welfare.asset.briefing.entity.Briefing;

import lk.AVSEC.Welfare.asset.briefing.service.BriefingService;
import lk.AVSEC.Welfare.asset.grievances.entity.Enum.Priority;
import lk.AVSEC.Welfare.util.interfaces.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/briefing")
public class BriefingController implements AbstractController<Briefing, Integer> {

    private final BriefingService briefingService;

    @Autowired
    public BriefingController(BriefingService briefingService) {
        this.briefingService = briefingService;
    }

    private String commonThing(Model model, Boolean booleanValue, Briefing briefingObject) {
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("addStatus", booleanValue);
        model.addAttribute("briefing", briefingObject);
        return "briefing/addBriefing";
    }
    //id, name, toWhom, notices, priority date
    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("briefings", briefingService.findAll());
        return "briefing/briefing";
    }

    @GetMapping("/add")
    public String form(Model model) {
        return commonThing(model, false, new Briefing());
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Integer id, Model model) {
        model.addAttribute("briefingDetail", briefingService.findById(id));
        return "briefing/briefing-detail";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        return commonThing(model, true, briefingService.findById(id));
    }

    @PostMapping(value = {"/save", "/update"})
    public String persist(@Valid @ModelAttribute Briefing briefing, BindingResult bindingResult,
                          RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            return commonThing(model, false, briefing);
        }
        redirectAttributes.addFlashAttribute("briefingDetail", briefingService.persist(briefing));
        return "redirect:/briefing";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, Model model) {
        briefingService.delete(id);
        return "redirect:/briefing";
    }


}
