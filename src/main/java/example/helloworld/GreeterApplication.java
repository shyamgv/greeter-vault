/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Sample Application using Spring Cloud Vault with Token authentication.
 *
 * @author Shyam
 */
@SpringBootApplication
@RestController
public class GreeterApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreeterApplication.class, args);
	}

	@Value("${mykey}")
	String mykey;

    @Autowired
    DataSource dataSource;

	@PostConstruct
	private void postConstruct() throws Exception{
		System.out.println("##########################");
		System.out.println(mykey);
		System.out.println("##########################");

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("SELECT * FROM users;");
			while (resultSet.next()){
				System.out.println("Connection works with User: "+resultSet.getString(1));
			}
            resultSet.close();
        }

        System.out.println("##########################");
	}

}
