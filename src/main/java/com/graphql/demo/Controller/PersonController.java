package com.graphql.demo.Controller;

import com.graphql.demo.Entity.PersonEntity;
import com.graphql.demo.Repository.PersonRepo;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
public class PersonController {
    @Autowired
    PersonRepo personRepo;
    @Value("classpath:person.graphqls")
    private Resource schemaResource;
    private GraphQL graphQL;
    @PostConstruct
    public void loadSchema() throws IOException {
        File schemaFile = schemaResource.getFile();
        TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);
        RuntimeWiring wiring = buildWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(registry,wiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    private RuntimeWiring buildWiring() {
        DataFetcher<List<PersonEntity>> fetcher1 = data->{
            return (List<PersonEntity>)personRepo.findAll();
        };
        DataFetcher<PersonEntity> fetcher2 = dataa->{
            return ( PersonEntity) personRepo.findByEmail(dataa.getArgument("email"));
        };
        return RuntimeWiring.newRuntimeWiring().type("Query",typewriting->typewriting.dataFetcher("getAllPerson",fetcher1).dataFetcher("findPerson",fetcher2)).build();
    }

    @PostMapping("/getAllPerson")
    public ResponseEntity<Object> allPersons(@RequestBody String query){
        ExecutionResult result = graphQL.execute(query);
        return new ResponseEntity<Object>(result,HttpStatus.OK);
    }
    @PostMapping("/getPerson")
    public ResponseEntity<Object> personsByEmail(@RequestBody String query){
        ExecutionResult result = graphQL.execute(query);
        return new ResponseEntity<Object>(result,HttpStatus.OK);
    }
    @PostMapping("/addperson")
    private  PersonEntity addNewPerson(@RequestBody PersonEntity pe){
        return personRepo.save(pe);

    }
    @GetMapping("/persons")
    private List<PersonEntity> allPerson(){
      return   personRepo.findAll();
    }
}
