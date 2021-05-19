package ru.cubly.iopc.configurator.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.cubly.iopc.configurator.model.AppConfig;
import ru.cubly.iopc.configurator.service.ConfigService;

import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Controller
@Slf4j
public class SettingsController {
    private final ConfigService configService;
    private final SmartValidator validator;

    public SettingsController(ConfigService configService, SmartValidator validator) {
        this.configService = configService;
        this.validator = validator;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("appConfig", configService.getAppConfig());
        model.addAttribute("moduleDescriptions", configService.getModuleDescriptions());
        model.addAttribute("reloadRequired", configService.isReloadRequired());
        return "pages/config";
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

    @SneakyThrows({ BindException.class })
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

}
