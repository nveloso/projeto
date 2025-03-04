package pt.isel.ps.gis.controllers;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isel.ps.gis.bll.AllergyService;
import pt.isel.ps.gis.model.Allergy;
import pt.isel.ps.gis.model.outputModel.AllergiesOutputModel;

import java.util.List;

import static pt.isel.ps.gis.utils.HeadersUtils.setSirenContentType;

@RestController
@RequestMapping("/v1/allergies")
public class AllergiesController {

    private final AllergyService allergyService;

    public AllergiesController(AllergyService allergyService) {
        this.allergyService = allergyService;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @GetMapping("")
    public ResponseEntity<AllergiesOutputModel> getAllergies() {
        List<Allergy> allergies = allergyService.getAllergies();
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(new AllergiesOutputModel(allergies), setSirenContentType(headers), HttpStatus.OK);
    }
}
