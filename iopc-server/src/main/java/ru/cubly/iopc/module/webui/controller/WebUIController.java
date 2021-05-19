package ru.cubly.iopc.module.webui.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.cubly.iopc.module.webui.dto.StatusDto;
import ru.cubly.iopc.module.webui.model.AppConfig;
import ru.cubly.iopc.module.webui.service.ConfigService;
import ru.cubly.iopc.module.webui.service.LogsService;

import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Controller
@RequiredArgsConstructor
@Log4j2
public class WebUIController {
    private final ConfigService configService;
    private final LogsService logsService;
    private final SmartValidator validator;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("appConfig", configService.getAppConfig());
        model.addAttribute("moduleDescriptions", configService.getModuleDescriptions());
        model.addAttribute("reloadRequired", configService.isReloadRequired());
        return "pages/config";
    }

    @GetMapping("/logs")
    public String logs(Model model) {
        model.addAttribute("logs", logsService.getAppLogs());
        return "pages/logs";
    }

    @PostMapping("/config/app")
    public String saveAppConfig(@Valid @ModelAttribute AppConfig appConfig,
                                RedirectAttributes redirectAttributes) {
        try {
            configService.saveAppConfig(appConfig);
            redirectAttributes.addFlashAttribute("configSaved", "app");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("configSaveError");
            log.error("Failed to save app config", e);
        }
        return "redirect:/";
    }

    @SneakyThrows({BindException.class})
    @PostMapping("/config/{moduleId}")
    public String saveModuleConfig(@PathVariable String moduleId,
                                   RedirectAttributes redirectAttributes,
                                   WebRequest request) {
        try {
            Object moduleConfig = configService.getEmptyModuleConfig(moduleId);

            WebRequestDataBinder binder = new WebRequestDataBinder(moduleConfig);
            binder.bind(request);

            this.validator.validate(moduleConfig, binder.getBindingResult());
            if (binder.getBindingResult().hasErrors()) {
                throw new BindException(binder.getBindingResult());
            }

            configService.saveModuleConfig(moduleId, moduleConfig);

            redirectAttributes.addFlashAttribute("configSaved", "module");
            redirectAttributes.addFlashAttribute("configSavedModule", moduleId);
        } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            redirectAttributes.addFlashAttribute("configSaveError");
            log.error("Failed to save module settings", e);
        }
        redirectAttributes.addFlashAttribute("configuredModuleId", moduleId);
        return "redirect:/";
    }

    @PostMapping("/restart")
    public String restart() {
        configService.scheduleRestart();
        return "pages/restart";
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public StatusDto status() {
        return new StatusDto(true);
    }
}
