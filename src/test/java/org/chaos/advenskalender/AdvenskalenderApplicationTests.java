package org.chaos.advenskalender;

import org.chaos.advenskalender.calendar.CalendarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdvenskalenderApplicationTests {

    @Autowired
    private CalendarService calendarService;

    @Test
    void contextLoads() throws Exception {

    }

}
