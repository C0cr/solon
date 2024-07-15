/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.swagger.demo.model.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author: lbq
 * 联系方式: 526509994@qq.com
 * 创建日期: 2020/9/17
 */
@ApiModel(description = "农机")
public class MachineDto {

    @ApiModelProperty(value = "农机id", example = "1")
    private String machineName;

    @ApiModelProperty(value = "农机状态 0正常 -1报废", example = "0")
    private int machineStatus;

    @ApiModelProperty(value = "农机局")
    private EquipDto equipDto;

    @ApiModelProperty(value = "农机局集合")
    private List<EquipDto> equipDtoList;

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public int getMachineStatus() {
        return machineStatus;
    }

    public void setMachineStatus(int machineStatus) {
        this.machineStatus = machineStatus;
    }

    public EquipDto getEquipDto() {
        return equipDto;
    }

    public void setEquipDto(EquipDto equipDto) {
        this.equipDto = equipDto;
    }

    public List<EquipDto> getEquipDtoList() {
        return equipDtoList;
    }

    public void setEquipDtoList(List<EquipDto> equipDtoList) {
        this.equipDtoList = equipDtoList;
    }
}

