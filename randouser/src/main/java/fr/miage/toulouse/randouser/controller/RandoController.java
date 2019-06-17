package fr.miage.toulouse.randouser.controller;

import fr.miage.toulouse.randouser.model.Rando;
import fr.miage.toulouse.randouser.model.Vote;
import fr.miage.toulouse.randouser.repo.RandoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class RandoController {

    @Autowired
    private RandoRepository randoRepository;

    @RequestMapping(path = "/randos", method = RequestMethod.GET)
    public Iterable<Rando> getRandos(){
        return randoRepository.findAll();
    }

    /**
     * Ajoute un vote pour la date d'une randonnée
     * @param idRando
     * @param date
     * @param userID
     * @return
     */
    @PostMapping(path="/api/vamarcher/1.0/rando/vote")
    public String voterPour(@RequestParam String idRando, @RequestParam String date, @RequestParam Integer userID){
        try{
            Rando rando = randoRepository.findbyRandoId(idRando);
            if (rando.getStatut() != "Vote ouvert"){
                throw  new Exception("Le vote n'est pas disponible pour cette randonnée");
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date tmp = formatter.parse(date);
            Timestamp timestamp = new Timestamp(tmp.getTime());
            Vote vote = new Vote(userID, timestamp);
            List<Vote> votes = rando.getPropositionsDates().get(timestamp.getTime());
            votes.add(vote);
            rando.getPropositionsDates().replace(timestamp.getTime(), votes);
            randoRepository.save(rando);
            return "vote OK";
        } catch (Exception e){
            return "KO";
        }
    }

    /**
     * Inscrption d'un randonneur à une randonnée.
     * @param idRando
     * @param userID
     * @return String : status
     */
    @PostMapping(path = "/api/vamarcher/1.0/rando/inscription")
    public String inscription (@RequestParam String idRando, @RequestParam Integer userID){
        try{
            Rando rando = randoRepository.findbyRandoId(idRando);
            rando.getParticipants().add(userID);
            randoRepository.save(rando);
            return "Inscription validée";
        } catch (Exception e){
            return "KO";
        }
    }

    /**
     *
     * @param idRando
     * @param userID
     * @return String : status
     */
    @DeleteMapping(path = "/api/vamarcher/1.0/rando/desinscription")
    public String desinscription(@RequestParam String idRando, @RequestParam Integer userID){
        try{
            Rando rando = randoRepository.findbyRandoId(idRando);
            if (!rando.getParticipants().contains(userID)){
                throw new Exception("L'utilisteur n'est pas inscrit");
            }
            int index = rando.getParticipants().indexOf(userID);
            rando.getParticipants().remove(index);
            return "Désinscription confirmée";
        }catch (Exception e){
            return "KO";
        }
    }
}
