/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.ipc;

import java.io.IOException;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.HBaseInterfaceAudience;
import org.apache.yetus.audience.InterfaceAudience;
import org.apache.yetus.audience.InterfaceStability;

import org.apache.hbase.thirdparty.com.google.protobuf.BlockingService;
import org.apache.hbase.thirdparty.com.google.protobuf.Descriptors.MethodDescriptor;
import org.apache.hbase.thirdparty.com.google.protobuf.Message;

import org.apache.hadoop.hbase.shaded.protobuf.generated.RPCProtos.RequestHeader;

/**
 * Interface of all necessary to carry out a RPC method invocation on the server.
 */
@InterfaceAudience.LimitedPrivate({ HBaseInterfaceAudience.COPROC, HBaseInterfaceAudience.PHOENIX })
@InterfaceStability.Evolving
public interface RpcCall extends RpcCallContext {

  /** Returns The service of this call. */
  BlockingService getService();

  /** Returns The service method. */
  MethodDescriptor getMethod();

  /** Returns The call parameter message. */
  Message getParam();

  /** Returns The CellScanner that can carry input and result payload. */
  CellScanner getCellScanner();

  /** Returns The timestamp when the call is constructed. */
  long getReceiveTime();

  /** Returns The time when the call starts to be executed. */
  long getStartTime();

  /**
   * Set the time when the call starts to be executed.
   */
  void setStartTime(long startTime);

  /** Returns The timeout of this call. */
  int getTimeout();

  /** Returns The Priority of this call. */
  int getPriority();

  /**
   * Return the deadline of this call. If we can not complete this call in time, we can throw a
   * TimeoutIOException and RPCServer will drop it.
   * @return The system timestamp of deadline.
   */
  long getDeadline();

  /**
   * Used to calculate the request call queue size. If the total request call size exceeds a limit,
   * the call will be rejected.
   * @return The raw size of this call.
   */
  long getSize();

  /** Returns The request header of this call. */
  RequestHeader getHeader();

  /**
   * Returns the map of attributes specified when building the Connection.
   * @see org.apache.hadoop.hbase.client.ConnectionFactory#createConnection(Configuration,
   *      ExecutorService, User, Map)
   */
  Map<String, byte[]> getConnectionAttributes();

  /**
   * Returns the map of attributes specified when building the request.
   * @see org.apache.hadoop.hbase.client.TableBuilder#setRequestAttribute(String, byte[])
   */
  Map<String, byte[]> getRequestAttributes();

  /** Returns Port of remote address in this call */
  int getRemotePort();

  /**
   * Set the response resulting from this RPC call.
   * @param param          The result message as response.
   * @param cells          The CellScanner that possibly carries the payload.
   * @param errorThrowable The error Throwable resulting from the call.
   * @param error          Extra error message.
   */
  void setResponse(Message param, CellScanner cells, Throwable errorThrowable, String error);

  /**
   * Send the response of this RPC call. Implementation provides the underlying facility
   * (connection, etc) to send.
   */
  void sendResponseIfReady() throws IOException;

  /**
   * Do the necessary cleanup after the call if needed.
   */
  void cleanup();

  /** Returns A short string format of this call without possibly lengthy params */
  String toShortString();
}
