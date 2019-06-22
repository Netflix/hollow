package com.netflix.vms.transformer.data.gen.gatekeeper2;

import com.netflix.hollow.api.testdata.HollowTestDataMapEntry;
import com.netflix.hollow.api.testdata.HollowTestDataset;

public class Gk2StatusTestData extends HollowTestDataset {

    public AvailableAssetsTestData AvailableAssets(AvailableAssetsTestData.AvailableAssetsField... fields) {
        AvailableAssetsTestData rec = AvailableAssetsTestData.AvailableAssets(fields);
        add(rec);
        return rec;
    }

    public DateTestData Date(DateTestData.DateField... fields) {
        DateTestData rec = DateTestData.Date(fields);
        add(rec);
        return rec;
    }

    public FlagsTestData Flags(FlagsTestData.FlagsField... fields) {
        FlagsTestData rec = FlagsTestData.Flags(fields);
        add(rec);
        return rec;
    }

    public ListOfRightsContractAssetTestData ListOfRightsContractAsset(RightsContractAssetTestData... elements) {
        ListOfRightsContractAssetTestData rec = ListOfRightsContractAssetTestData.ListOfRightsContractAsset(elements);
        add(rec);
        return rec;
    }

    public ListOfRightsContractPackageTestData ListOfRightsContractPackage(RightsContractPackageTestData... elements) {
        ListOfRightsContractPackageTestData rec = ListOfRightsContractPackageTestData.ListOfRightsContractPackage(elements);
        add(rec);
        return rec;
    }

    public ListOfRightsWindowTestData ListOfRightsWindow(RightsWindowTestData... elements) {
        ListOfRightsWindowTestData rec = ListOfRightsWindowTestData.ListOfRightsWindow(elements);
        add(rec);
        return rec;
    }

    public ListOfRightsWindowContractTestData ListOfRightsWindowContract(RightsWindowContractTestData... elements) {
        ListOfRightsWindowContractTestData rec = ListOfRightsWindowContractTestData.ListOfRightsWindowContract(elements);
        add(rec);
        return rec;
    }

    public ListOfStringTestData ListOfString(StringTestData... elements) {
        ListOfStringTestData rec = ListOfStringTestData.ListOfString(elements);
        add(rec);
        return rec;
    }

    public MapKeyTestData MapKey(MapKeyTestData.MapKeyField... fields) {
        MapKeyTestData rec = MapKeyTestData.MapKey(fields);
        add(rec);
        return rec;
    }

    @SafeVarargs
    public final MapOfFlagsFirstDisplayDatesTestData MapOfFlagsFirstDisplayDates(HollowTestDataMapEntry<MapKeyTestData, DateTestData>... entries) {
        MapOfFlagsFirstDisplayDatesTestData rec = MapOfFlagsFirstDisplayDatesTestData.MapOfFlagsFirstDisplayDates(entries);
        add(rec);
        return rec;
    }

    public ParentNodeIdTestData ParentNodeId(ParentNodeIdTestData.ParentNodeIdField... fields) {
        ParentNodeIdTestData rec = ParentNodeIdTestData.ParentNodeId(fields);
        add(rec);
        return rec;
    }

    public RightsTestData Rights(RightsTestData.RightsField... fields) {
        RightsTestData rec = RightsTestData.Rights(fields);
        add(rec);
        return rec;
    }

    public RightsContractAssetTestData RightsContractAsset(RightsContractAssetTestData.RightsContractAssetField... fields) {
        RightsContractAssetTestData rec = RightsContractAssetTestData.RightsContractAsset(fields);
        add(rec);
        return rec;
    }

    public RightsContractPackageTestData RightsContractPackage(RightsContractPackageTestData.RightsContractPackageField... fields) {
        RightsContractPackageTestData rec = RightsContractPackageTestData.RightsContractPackage(fields);
        add(rec);
        return rec;
    }

    public RightsWindowTestData RightsWindow(RightsWindowTestData.RightsWindowField... fields) {
        RightsWindowTestData rec = RightsWindowTestData.RightsWindow(fields);
        add(rec);
        return rec;
    }

    public RightsWindowContractTestData RightsWindowContract(RightsWindowContractTestData.RightsWindowContractField... fields) {
        RightsWindowContractTestData rec = RightsWindowContractTestData.RightsWindowContract(fields);
        add(rec);
        return rec;
    }

    public SetOfStringTestData SetOfString(StringTestData... elements) {
        SetOfStringTestData rec = SetOfStringTestData.SetOfString(elements);
        add(rec);
        return rec;
    }

    public StatusTestData Status(StatusTestData.StatusField... fields) {
        StatusTestData rec = StatusTestData.Status(fields);
        add(rec);
        return rec;
    }

    public StringTestData String(StringTestData.StringField... fields) {
        StringTestData rec = StringTestData.String(fields);
        add(rec);
        return rec;
    }

    public VideoHierarchyInfoTestData VideoHierarchyInfo(VideoHierarchyInfoTestData.VideoHierarchyInfoField... fields) {
        VideoHierarchyInfoTestData rec = VideoHierarchyInfoTestData.VideoHierarchyInfo(fields);
        add(rec);
        return rec;
    }

    public VideoNodeTypeTestData VideoNodeType(VideoNodeTypeTestData.VideoNodeTypeField... fields) {
        VideoNodeTypeTestData rec = VideoNodeTypeTestData.VideoNodeType(fields);
        add(rec);
        return rec;
    }

}