package teledon.services.rest;

import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import teledon.model.CharityCase;
import teledon.persistence.hibernate.CharityCaseHibernateRepository;
import teledon.persistence.interfaces.ICharityCaseRepository;
import teledon.services.notification.TeledonNotificationService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Component
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/teledon/charity-cases")
public class CharityCaseController {

    @Autowired
    private CharityCaseHibernateRepository charityCaseRepository;

    @Autowired
    private TeledonNotificationService teledonNotificationService;
    @RequestMapping(method = RequestMethod.POST)
    public CharityCase create(@RequestBody CharityCase charityCase) {
        CharityCase savedCase = charityCaseRepository.save(charityCase);
        notifyChanges();
        return savedCase;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<CharityCase> getAllCases() {
        List<CharityCase> charityCases = (List<CharityCase>) charityCaseRepository.findAll();
        return charityCases;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getCaseById(@PathVariable Integer id) {
        CharityCase foundCase = charityCaseRepository.findOne(id);
        if (foundCase == null) {
            return new ResponseEntity<String>("Case not found",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<CharityCase>(foundCase, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<CharityCase> updateCase(@PathVariable Integer id, @RequestBody CharityCase updatedCase) {
        updatedCase.setId(id);
        CharityCase result = charityCaseRepository.update(updatedCase);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        notifyChanges();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCase(@PathVariable Integer id) {
        CharityCase deleted = charityCaseRepository.delete(id);
        if (deleted == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        notifyChanges();
        return new ResponseEntity<String>("Case deleted",HttpStatus.OK);
    }
    private void notifyChanges(){
        Iterable<CharityCase> iterable = charityCaseRepository.findAll();
        List<CharityCase> list = new ArrayList<>();
        iterable.forEach(list::add);
        CharityCase[] array = list.toArray(new CharityCase[0]);
        teledonNotificationService.charityCasesUpdated(array);
    }
    @RequestMapping("/{caseName}/name")
    public String name(@PathVariable String caseName) {
        CharityCase result=charityCaseRepository.findByName(caseName);
        System.out.println("Result ..."+result);
        return result.getName();
    }
}
