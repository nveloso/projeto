package pt.isel.ps.gis.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.isel.ps.gis.bll.InvitationService;
import pt.isel.ps.gis.components.AuthenticationFacade;
import pt.isel.ps.gis.exceptions.*;
import pt.isel.ps.gis.model.Invitation;
import pt.isel.ps.gis.model.inputModel.InvitationInputModel;
import pt.isel.ps.gis.model.inputModel.InvitationInputModelUpdate;
import pt.isel.ps.gis.model.outputModel.InvitationsOutputModel;

import java.util.List;
import java.util.Locale;

import static pt.isel.ps.gis.utils.HeadersUtils.setSirenContentType;

@RestController
@RequestMapping("/v1/invitations")
public class InvitationController {

    private final InvitationService invitationService;
    private final AuthenticationFacade authenticationFacade;

    public InvitationController(InvitationService invitationService, AuthenticationFacade authenticationFacade) {
        this.invitationService = invitationService;
        this.authenticationFacade = authenticationFacade;
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<InvitationsOutputModel> getInvitations(
            @PathVariable("username") String username
    ) throws BadRequestException, NotFoundException {
        List<Invitation> invitations;
        try {
            invitations = invitationService.getReceivedInvitationsByUserUsername(username);
        } catch (EntityException e) {
            throw new BadRequestException(e.getMessage(), e.getUserFriendlyMessage());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage(), e.getUserFriendlyMessage());
        }
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(new InvitationsOutputModel(username, invitations), setSirenContentType(headers),
                HttpStatus.OK);
    }

    @PostMapping("/houses/{house-id}")
    public ResponseEntity postInvitation(
            @PathVariable("house-id") long houseId,
            @RequestBody InvitationInputModel body,
            Locale locale
    ) throws BadRequestException, NotFoundException, ConflictException, ForbiddenException {
        String username = authenticationFacade.getAuthentication().getName();
        try {
            invitationService.sentInvitation(houseId, username, body.getUsername(), locale);
        } catch (EntityException e) {
            throw new BadRequestException(e.getMessage(), e.getUserFriendlyMessage());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage(), e.getUserFriendlyMessage());
        } catch (EntityAlreadyExistsException e) {
            throw new ConflictException(e.getMessage(), e.getUserFriendlyMessage());
        } catch (InsufficientPrivilegesException e) {
            throw new ForbiddenException(e.getMessage(), e.getUserFriendlyMessage());
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping("/houses/{house-id}/users/{username}")
    public ResponseEntity putInvitation(
            @PathVariable("house-id") Long houseId,
            @PathVariable("username") String username,
            @RequestBody InvitationInputModelUpdate body,
            Locale locale
    ) throws BadRequestException, NotFoundException {
        try {
            invitationService.updateInvitation(houseId, username, body.getAccept(), locale);
        } catch (EntityException e) {
            throw new BadRequestException(e.getMessage(), e.getUserFriendlyMessage());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage(), e.getUserFriendlyMessage());
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
