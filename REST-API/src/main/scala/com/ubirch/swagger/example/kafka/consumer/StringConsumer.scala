package com.ubirch.swagger.example.kafka.consumer

import org.apache.kafka.clients.consumer.ConsumerRecord


class StringConsumer {

  case class PipeData(consumerRecords: Vector[ConsumerRecord[String, String]], eventLog: Option[EventLog]) extends EventLogPipeData[String]


}
