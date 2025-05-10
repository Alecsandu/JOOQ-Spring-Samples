package com.dockerino.demo.data;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/data")
public class DataApi {

    private final DataService dataService;

    public DataApi(DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping
    public ResponseEntity<String> addData(@RequestBody String payload) {
        return ResponseEntity.ok(dataService.addData(payload));
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getDataById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(dataService.getDataById(id));
    }

    @GetMapping
    public ResponseEntity<List<String>> getDataAllOps() {
        return ResponseEntity.ok(dataService.getData());
    }

}
