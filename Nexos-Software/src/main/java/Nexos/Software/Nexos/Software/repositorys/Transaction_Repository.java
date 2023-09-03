package Nexos.Software.Nexos.Software.repositorys;

import Nexos.Software.Nexos.Software.entitys.Transaction_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface Transaction_Repository extends JpaRepository<Transaction_Entity,String>{





}
