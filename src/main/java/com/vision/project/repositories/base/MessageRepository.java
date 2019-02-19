package com.vision.project.repositories.base;

import com.vision.project.models.Chat;
import com.vision.project.models.Message;
import com.vision.project.models.compositePK.MessagePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, MessagePK> {
}
