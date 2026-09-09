package com.nexora.kaamsetu.data.local

/**
 * One-time demo/mock data. This stands in for the future backend during the
 * test-APK phase (see MVP scope: mock/local data now, remote repository later).
 */
object SeedData {

    fun categories(): List<ServiceCategoryEntity> = listOf(
        ServiceCategoryEntity("electrician", "Home Repair", "Electrician"),
        ServiceCategoryEntity("plumber", "Home Repair", "Plumber"),
        ServiceCategoryEntity("carpenter", "Home Repair", "Carpenter"),
        ServiceCategoryEntity("painter", "Home Repair", "Painter"),
        ServiceCategoryEntity("mason", "Home Repair", "Mason"),
        ServiceCategoryEntity("welder", "Home Repair", "Welder"),
        ServiceCategoryEntity("locksmith", "Home Repair", "Locksmith"),
        ServiceCategoryEntity("gardener", "Home Repair", "Gardener"),
        ServiceCategoryEntity("ac_repair", "Appliance Repair", "AC Repair"),
        ServiceCategoryEntity("cooler_repair", "Appliance Repair", "Cooler Repair"),
        ServiceCategoryEntity("fridge_repair", "Appliance Repair", "Refrigerator Repair"),
        ServiceCategoryEntity("washing_machine_repair", "Appliance Repair", "Washing Machine Repair"),
        ServiceCategoryEntity("fan_repair", "Appliance Repair", "Fan Repair"),
        ServiceCategoryEntity("tv_repair", "Appliance Repair", "TV Repair"),
        ServiceCategoryEntity("mobile_repair", "Technology", "Mobile Repair"),
        ServiceCategoryEntity("computer_repair", "Technology", "Computer Repair"),
        // Phase 1 ids kept as-is (technician skills reference them); display
        // names updated in Phase 2 to match the product's demo service list.
        ServiceCategoryEntity("wifi_setup", "Technology", "Wi-Fi Technician"),
        ServiceCategoryEntity("printer_repair", "Technology", "Printer Repair"),
        ServiceCategoryEntity("cctv_install", "Technology", "CCTV Technician"),
        ServiceCategoryEntity("home_cleaning", "Cleaning", "Home Cleaning"),
        ServiceCategoryEntity("cleaner", "Cleaning", "Cleaner"),
        ServiceCategoryEntity("bathroom_cleaning", "Cleaning", "Bathroom Cleaning"),
        ServiceCategoryEntity("sofa_cleaning", "Cleaning", "Sofa Cleaning"),
        ServiceCategoryEntity("water_tank_cleaning", "Cleaning", "Water Tank Cleaning"),
        ServiceCategoryEntity("bike_mechanic", "Vehicle", "Bike Mechanic"),
        ServiceCategoryEntity("car_mechanic", "Vehicle", "Car Mechanic")
    )

    fun technicians(): List<TechnicianEntity> = listOf(
        TechnicianEntity(
            id = "tech_1", name = "Ramesh Kumar", phone = "9990000001",
            status = "APPROVED", skillsCsv = "electrician,fan_repair", city = "Lucknow",
            rating = 4.6, completedJobs = 128, isVerified = true, isAvailable = true
        ),
        TechnicianEntity(
            id = "tech_2", name = "Suresh Yadav", phone = "9990000002",
            status = "APPROVED", skillsCsv = "plumber,water_tank_cleaning", city = "Lucknow",
            rating = 4.3, completedJobs = 76, isVerified = true, isAvailable = true
        ),
        TechnicianEntity(
            id = "tech_3", name = "Vikram Singh", phone = "9990000003",
            status = "PENDING", skillsCsv = "ac_repair,cooler_repair", city = "Kanpur",
            rating = 0.0, completedJobs = 0, isVerified = false, isAvailable = false
        ),
        TechnicianEntity(
            id = "tech_4", name = "Anil Sharma", phone = "9990000004",
            status = "APPROVED", skillsCsv = "mobile_repair,computer_repair", city = "Lucknow",
            rating = 4.8, completedJobs = 210, isVerified = true, isAvailable = true
        )
    )

    fun customers(): List<CustomerEntity> = listOf(
        CustomerEntity(id = "cust_demo", name = "Demo Customer", phone = "9998887770")
    )

    /**
     * A couple of illustrative jobs so My Jobs / Admin Jobs aren't empty on
     * first launch. Phase 2's real flow is creating NEW requests through the
     * app itself (Home -> Services -> Create Request) — this seed data is not
     * regenerated on every launch beyond the initial insert.
     */
    fun jobRequests(): List<JobRequestEntity> = listOf(
        JobRequestEntity(
            id = "job_1",
            customerId = "cust_demo",
            serviceId = "electrician",
            serviceName = "Electrician",
            problemDescription = "Ceiling fan not switching on, possible wiring issue.",
            preferredTime = "TODAY",
            scheduledDateTime = null,
            serviceType = "HOME_VISIT",
            approximateArea = "Gomti Nagar, Lucknow",
            exactAddress = "House 12, Sector 4, Gomti Nagar, Lucknow",
            customerName = "Demo Customer",
            customerPhone = "9998887770",
            status = "QUOTES_RECEIVED",
            createdAt = System.currentTimeMillis() - 3_600_000L
        ),
        JobRequestEntity(
            id = "job_2",
            customerId = "cust_demo",
            serviceId = "plumber",
            serviceName = "Plumber",
            problemDescription = "Kitchen tap leaking continuously.",
            preferredTime = "NOW",
            scheduledDateTime = null,
            serviceType = "HOME_VISIT",
            approximateArea = "Indira Nagar, Lucknow",
            exactAddress = "Flat 302, Shanti Apartments, Indira Nagar, Lucknow",
            customerName = "Demo Customer",
            customerPhone = "9998887770",
            status = "REQUEST_CREATED",
            createdAt = System.currentTimeMillis() - 1_800_000L
        )
    )

    fun quotes(): List<QuoteEntity> = listOf(
        QuoteEntity(id = "quote_1", jobRequestId = "job_1", technicianId = "tech_1", price = 350.0, etaMinutes = 40),
        QuoteEntity(id = "quote_2", jobRequestId = "job_1", technicianId = "tech_4", price = 400.0, etaMinutes = 25)
    )
}
