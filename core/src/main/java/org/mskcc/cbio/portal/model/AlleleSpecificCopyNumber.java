/*
 * Copyright (c) 2019 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.mskcc.cbio.portal.model;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author averyniceday.
 */
public class AlleleSpecificCopyNumber implements Serializable {

    private long mutationEventId;
    private int geneticProfileId;
    private int sampleId;
    private Integer ascnIntegerCopyNumber;
    private String ascnMethod;
    private Float ccfMCopiesUpper;
    private Float ccfMCopies;
    private Boolean clonal;
    private Integer minorCopyNumber;
    private Integer mutantCopies;
    private Integer totalCopyNumber;

    public AlleleSpecificCopyNumber(Map<String,String> ascnData) {
        System.out.println("Constructing ascn");
        for (String key : ascnData.keySet()) {
            System.out.println(key + " ======== " + ascnData.get(key));
        }
        this.ascnIntegerCopyNumber = (!ascnData.get("ascn_integer_copy_number").isEmpty() ? Integer.parseInt(ascnData.get("ascn_integer_copy_number")) : null);
        this.ascnMethod = (!ascnData.get("ascn_method").isEmpty() ? ascnData.get("ascn_method") : null);
        this.ccfMCopiesUpper = (!ascnData.get("ccf_m_copies_upper").isEmpty() ? Float.parseFloat(ascnData.get("ccf_m_copies_upper")) : null);
        this.ccfMCopies = (!ascnData.get("ccf_m_copies").isEmpty() ? Float.parseFloat(ascnData.get("ccf_m_copies")) : null);
        this.clonal = (!ascnData.get("clonal").isEmpty() ? Boolean.parseBoolean(ascnData.get("clonal")) : null);
        this.minorCopyNumber = (!ascnData.get("minor_copy_number").isEmpty() ? Integer.parseInt(ascnData.get("minor_copy_number")) : null);
        this.mutantCopies = (!ascnData.get("mutant_copies").isEmpty() ? Integer.parseInt(ascnData.get("mutant_copies")) : null);
        this.totalCopyNumber = (!ascnData.get("total_copy_number").isEmpty() ? Integer.parseInt(ascnData.get("total_copy_number")) : null);
    }

    public void updateAscnUniqueKeyDetails(ExtendedMutation mutation) {
        this.mutationEventId = mutation.getMutationEventId();
        this.geneticProfileId = mutation.getGeneticProfileId();
        this.sampleId = mutation.getSampleId();
    }

    public long getMutationEventId() {
        return mutationEventId;
    }

    public void setMutationEventId(long mutationEventId) {
        this.mutationEventId = mutationEventId;
    }

    public int getGeneticProfileId() {
        return geneticProfileId;
    }

    public void setGeneticProfileId(int geneticProfileId) {
        this.geneticProfileId = geneticProfileId;
    }

    public int getSampleId() {
        return sampleId;
    }

    public void setSampleId(int sampleId) {
        this.sampleId = sampleId;
    }

    public Integer getAscnIntegerCopyNumber() {
        return ascnIntegerCopyNumber;
    }

    public void setAscnIntegerCopyNumber(Integer ascnIntegerCopyNumber) {
        this.ascnIntegerCopyNumber = ascnIntegerCopyNumber;
    }

    public String getAscnMethod() {
        return ascnMethod;
    }

    public void setAscnMethod(String ascnMethod) {
        this.ascnMethod = ascnMethod;
    }

    public Float getCcfMCopiesUpper() {
        return ccfMCopiesUpper;
    }

    public void setCcfMCopiesUpper(Float ccfMCopiesUpper) {
        this.ccfMCopiesUpper = ccfMCopiesUpper;
    }

    public Float getCcfMCopies() {
        return ccfMCopies;
    }

    public void setCcfMCopies(Float ccfMCopies) {
        this.ccfMCopies = ccfMCopies;
    }

    public Boolean getClonal() {
        return clonal;
    }

    public void setClonal(Boolean clonal) {
        this.clonal = clonal;
    }

    public Integer getMinorCopyNumber() {
        return minorCopyNumber;
    }

    public void setMinorCopyNumber(Integer minorCopyNumber) {
        this.minorCopyNumber = minorCopyNumber;
    }

    public Integer getMutantCopies() {
        return mutantCopies;
    }

    public void setMutantCopies(Integer mutantCopies) {
        this.mutantCopies = mutantCopies;
    }

    public Integer getTotalCopyNumber() {
        return totalCopyNumber;
    }

    public void setTotalCopyNumber(Integer totalCopyNumber) {
        this.totalCopyNumber = totalCopyNumber;
    }
}
